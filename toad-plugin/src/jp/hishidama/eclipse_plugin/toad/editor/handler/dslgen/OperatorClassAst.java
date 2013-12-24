package jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen;

import java.io.Closeable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen.OperatorMethodGenerator.TypeResolver;
import jp.hishidama.eclipse_plugin.util.DocumentUtil;
import jp.hishidama.xtext.dmdl_editor.ui.internal.LogUtil;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.ui.CodeStyleConfiguration;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

public class OperatorClassAst implements Closeable {

	private final IType type;
	private final IFile file;

	private CompilationUnit compilationUnit;
	private ImportRewrite importRewrite;
	private TypeDeclaration typeDeclaration;
	private Map<String, MethodDeclaration> methodMap;

	private IDocument document;
	private ITextFileBufferManager manager;
	private ITextFileBuffer buffer;

	public OperatorClassAst(IType type) {
		this.type = type;
		this.file = (IFile) type.getResource();
	}

	@SuppressWarnings("unchecked")
	public void addMethod(OperatorMethodGenerator gen) {
		initializeCompilationUnit();

		MethodDeclaration sourceMethod = getSourceMethod(gen);

		AST ast = compilationUnit.getAST();

		MethodDeclaration method = findMethod(gen.getMethodName());
		boolean exists = true;
		if (method == null) {
			exists = false;
			method = ast.newMethodDeclaration();
			method.setName(ast.newSimpleName(gen.getMethodName()));
			methodMap.put(gen.getMethodName(), method);
			List<BodyDeclaration> list = typeDeclaration.bodyDeclarations();
			list.add(method);
		}

		method.setJavadoc(NewAstNode.copyFrom(ast, sourceMethod.getJavadoc()));

		List<IExtendedModifier> mod = method.modifiers();
		mod.clear();
		mod.addAll(NewAstNode.copyFrom(ast, sourceMethod.modifiers()));

		method.setReturnType2(NewAstNode.copyFrom(ast, sourceMethod.getReturnType2()));

		List<SingleVariableDeclaration> paramList = method.parameters();
		paramList.clear();
		paramList.addAll(NewAstNode.copyFrom(ast, sourceMethod.parameters()));

		if (gen.isAbstract()) {
			method.setBody(null);
		} else if (!exists) {
			method.setBody(NewAstNode.copyFrom(ast, sourceMethod.getBody()));
		}
	}

	private MethodDeclaration getSourceMethod(OperatorMethodGenerator gen) {
		gen.setProject(type.getJavaProject().getProject());

		TypeResolver resolver = new TypeResolver() {
			@Override
			public String resolve(String type) {
				return importRewrite.addImport(type);
			}
		};
		String source = gen.toSourceString(resolver);

		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(source.toCharArray());
		parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
		ASTNode sourceAst = parser.createAST(null);
		MethodDeclaration sourceMethod = MethodFinder.findMethod(sourceAst);

		return sourceMethod;
	}

	private void initializeCompilationUnit() {
		if (compilationUnit == null) {
			ASTParser parser = ASTParser.newParser(AST.JLS4);
			parser.setSource(type.getCompilationUnit());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			compilationUnit = (CompilationUnit) parser.createAST(null);
			compilationUnit.recordModifications();
			importRewrite = CodeStyleConfiguration.createImportRewrite(compilationUnit, true);

			methodMap = new HashMap<String, MethodDeclaration>();
			compilationUnit.accept(new InitializeVisitor());
		}
	}

	private class InitializeVisitor extends ASTVisitor {
		@Override
		public boolean visit(TypeDeclaration node) {
			typeDeclaration = node;
			return true;
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			methodMap.put(node.getName().getIdentifier(), node);
			return false;
		}
	}

	private MethodDeclaration findMethod(String methodName) {
		return methodMap.get(methodName);
	}

	@Override
	public void close() {
		try {
			if (compilationUnit != null) {
				IDocument doc = getDocument();
				if (doc != null) {
					try {
						TextEdit edit = compilationUnit.rewrite(doc, null);
						edit.apply(doc);
						TextEdit edit2 = importRewrite.rewriteImports(null);
						edit2.apply(doc);
					} catch (Exception e) {
						LogUtil.logWarn("document apply error.", e);
					}
				}
			}
		} finally {
			closeDocument();
		}
	}

	private IDocument getDocument() {
		if (document == null) {
			document = DocumentUtil.findEditorDocument(file);
			if (document == null) {
				document = createDocument();
			}
		}
		return document;
	}

	private IDocument createDocument() {
		this.manager = FileBuffers.getTextFileBufferManager();
		try {
			IPath path = file.getFullPath();
			manager.connect(path, LocationKind.IFILE, null);
			this.buffer = manager.getTextFileBuffer(path, LocationKind.IFILE);
			return buffer.getDocument();
		} catch (CoreException e) {
			return null;
		}
	}

	private void closeDocument() {
		if (buffer != null) {
			try {
				buffer.commit(null, true);
			} catch (CoreException e) {
				LogUtil.log(e.getStatus());
			}
		}
		if (manager != null) {
			try {
				IPath path = file.getFullPath();
				manager.disconnect(path, LocationKind.IFILE, null);
			} catch (CoreException e) {
				LogUtil.log(e.getStatus());
			}
		}
	}

	private static class MethodFinder extends ASTVisitor {

		public static MethodDeclaration findMethod(ASTNode node) {
			MethodFinder finder = new MethodFinder();
			node.accept(finder);
			return finder.method;
		}

		private MethodDeclaration method;

		@Override
		public boolean visit(MethodDeclaration node) {
			this.method = node;
			return false;
		}
	}
}
