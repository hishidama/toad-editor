package jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen;

import java.util.ArrayList;
import java.util.List;

import jp.hishidama.eclipse_plugin.toad.internal.LogUtil;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;

@SuppressWarnings("unchecked")
public class NewAstNode<T extends ASTNode> extends ASTVisitor {

	public static <T extends ASTNode> List<T> copyFrom(AST ast, List<T> list) {
		List<T> result = new ArrayList<T>(list.size());
		for (T node : list) {
			T r = copyFrom(ast, node);
			if (r == null) {
				continue;
			}
			result.add(r);
		}
		return result;
	}

	public static <T extends ASTNode> T copyFrom(AST ast, T node) {
		if (node == null) {
			return null;
		}
		NewAstNode<T> visitor = new NewAstNode<T>(ast);
		node.accept(visitor);
		return visitor.getResult();
	}

	private final AST ast;
	private T result;

	public NewAstNode(AST ast) {
		this.ast = ast;
	}

	public T getResult() {
		return result;
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		return visitNode(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		return visitNode(node);
	}

	public boolean visit(AnonymousClassDeclaration node) {
		return visitNode(node);
	}

	public boolean visit(ArrayAccess node) {
		return visitNode(node);
	}

	public boolean visit(ArrayCreation node) {
		return visitNode(node);
	}

	public boolean visit(ArrayInitializer node) {
		ArrayInitializer a = ast.newArrayInitializer();
		a.expressions().addAll(copyFrom(ast, node.expressions()));
		result = (T) a;
		return false;
	}

	public boolean visit(ArrayType node) {
		visitNode(node);
		return false;
	}

	public boolean visit(AssertStatement node) {
		return visitNode(node);
	}

	public boolean visit(Assignment node) {
		return visitNode(node);
	}

	public boolean visit(Block node) {
		Block b = ast.newBlock();
		b.statements().addAll(copyFrom(ast, node.statements()));
		result = (T) b;
		return false;
	}

	/* since 3.0 */
	public boolean visit(BlockComment node) {
		return visitNode(node);
	}

	public boolean visit(BooleanLiteral node) {
		return visitNode(node);
	}

	public boolean visit(BreakStatement node) {
		return visitNode(node);
	}

	public boolean visit(CastExpression node) {
		return visitNode(node);
	}

	public boolean visit(CatchClause node) {
		return visitNode(node);
	}

	public boolean visit(CharacterLiteral node) {
		return visitNode(node);
	}

	public boolean visit(ClassInstanceCreation node) {
		return visitNode(node);
	}

	public boolean visit(CompilationUnit node) {
		return visitNode(node);
	}

	public boolean visit(ConditionalExpression node) {
		return visitNode(node);
	}

	public boolean visit(ConstructorInvocation node) {
		return visitNode(node);
	}

	public boolean visit(ContinueStatement node) {
		return visitNode(node);
	}

	public boolean visit(DoStatement node) {
		return visitNode(node);
	}

	public boolean visit(EmptyStatement node) {
		return visitNode(node);
	}

	public boolean visit(EnhancedForStatement node) {
		return visitNode(node);
	}

	public boolean visit(EnumConstantDeclaration node) {
		return visitNode(node);
	}

	public boolean visit(EnumDeclaration node) {
		return visitNode(node);
	}

	public boolean visit(ExpressionStatement node) {
		return visitNode(node);
	}

	public boolean visit(FieldAccess node) {
		return visitNode(node);
	}

	public boolean visit(FieldDeclaration node) {
		return visitNode(node);
	}

	public boolean visit(ForStatement node) {
		return visitNode(node);
	}

	public boolean visit(IfStatement node) {
		return visitNode(node);
	}

	public boolean visit(ImportDeclaration node) {
		return visitNode(node);
	}

	public boolean visit(InfixExpression node) {
		return visitNode(node);
	}

	public boolean visit(Initializer node) {
		return visitNode(node);
	}

	public boolean visit(InstanceofExpression node) {
		return visitNode(node);
	}

	public boolean visit(Javadoc node) {
		Javadoc d = ast.newJavadoc();
		d.tags().addAll(copyFrom(ast, node.tags()));
		result = (T) d;
		return false;
	}

	public boolean visit(LabeledStatement node) {
		return visitNode(node);
	}

	public boolean visit(LineComment node) {
		return visitNode(node);
	}

	public boolean visit(MarkerAnnotation node) {
		MarkerAnnotation a = ast.newMarkerAnnotation();
		Name typeName = copyFrom(ast, node.getTypeName());
		a.setTypeName(typeName);
		result = (T) a;
		return false;
	}

	public boolean visit(MemberRef node) {
		return visitNode(node);
	}

	public boolean visit(MemberValuePair node) {
		MemberValuePair v = ast.newMemberValuePair();
		v.setName(copyFrom(ast, node.getName()));
		v.setValue(copyFrom(ast, node.getValue()));
		result = (T) v;
		return false;
	}

	public boolean visit(MethodDeclaration node) {
		return visitNode(node);
	}

	public boolean visit(MethodInvocation node) {
		return visitNode(node);
	}

	public boolean visit(MethodRef node) {
		return visitNode(node);
	}

	public boolean visit(Modifier node) {
		result = (T) ast.newModifier(node.getKeyword());
		return false;
	}

	public boolean visit(MethodRefParameter node) {
		return visitNode(node);
	}

	public boolean visit(NormalAnnotation node) {
		NormalAnnotation a = ast.newNormalAnnotation();
		a.setTypeName(copyFrom(ast, node.getTypeName()));
		a.values().addAll(copyFrom(ast, node.values()));
		result = (T) a;
		return false;
	}

	public boolean visit(NullLiteral node) {
		result = (T) ast.newNullLiteral();
		return true;
	}

	public boolean visit(NumberLiteral node) {
		return visitNode(node);
	}

	public boolean visit(PackageDeclaration node) {
		return visitNode(node);
	}

	public boolean visit(ParameterizedType node) {
		Type type = copyFrom(ast, node.getType());
		ParameterizedType t = ast.newParameterizedType(type);
		List<Type> arguments = t.typeArguments();
		arguments.addAll(copyFrom(ast, node.typeArguments()));
		result = (T) t;
		return false;
	}

	public boolean visit(ParenthesizedExpression node) {
		return visitNode(node);
	}

	public boolean visit(PostfixExpression node) {
		return visitNode(node);
	}

	public boolean visit(PrefixExpression node) {
		return visitNode(node);
	}

	public boolean visit(PrimitiveType node) {
		result = (T) ast.newPrimitiveType(node.getPrimitiveTypeCode());
		return false;
	}

	public boolean visit(QualifiedName node) {
		Name qualifier = copyFrom(ast, node.getQualifier());
		SimpleName name = copyFrom(ast, node.getName());
		result = (T) ast.newQualifiedName(qualifier, name);
		return false;
	}

	public boolean visit(QualifiedType node) {
		Type qualifier = copyFrom(ast, node.getQualifier());
		SimpleName name = copyFrom(ast, node.getName());
		result = (T) ast.newQualifiedType(qualifier, name);
		return false;
	}

	public boolean visit(ReturnStatement node) {
		ReturnStatement r = ast.newReturnStatement();
		r.setExpression(copyFrom(ast, node.getExpression()));
		result = (T) r;
		return false;
	}

	public boolean visit(SimpleName node) {
		result = (T) ast.newSimpleName(node.getIdentifier());
		return false;
	}

	public boolean visit(SimpleType node) {
		Name typeName = copyFrom(ast, node.getName());
		result = (T) ast.newSimpleType(typeName);
		return false;
	}

	public boolean visit(SingleMemberAnnotation node) {
		return visitNode(node);
	}

	public boolean visit(SingleVariableDeclaration node) {
		SingleVariableDeclaration d = ast.newSingleVariableDeclaration();
		List<IExtendedModifier> mod = d.modifiers();
		mod.addAll(copyFrom(ast, node.modifiers()));
		d.setType(copyFrom(ast, node.getType()));
		d.setName(copyFrom(ast, node.getName()));
		d.setExtraDimensions(node.getExtraDimensions());
		d.setInitializer(copyFrom(ast, node.getInitializer()));
		d.setVarargs(node.isVarargs());
		result = (T) d;
		return false;
	}

	public boolean visit(StringLiteral node) {
		StringLiteral s = ast.newStringLiteral();
		s.setEscapedValue(node.getEscapedValue());
		result = (T) s;
		return false;
	}

	public boolean visit(SuperConstructorInvocation node) {
		return visitNode(node);
	}

	public boolean visit(SuperFieldAccess node) {
		return visitNode(node);
	}

	public boolean visit(SuperMethodInvocation node) {
		return visitNode(node);
	}

	public boolean visit(SwitchCase node) {
		return visitNode(node);
	}

	public boolean visit(SwitchStatement node) {
		return visitNode(node);
	}

	public boolean visit(SynchronizedStatement node) {
		return visitNode(node);
	}

	public boolean visit(TagElement node) {
		TagElement t = ast.newTagElement();
		t.setTagName(node.getTagName());
		t.fragments().addAll(copyFrom(ast, node.fragments()));
		result = (T) t;
		return false;
	}

	public boolean visit(TextElement node) {
		TextElement t = ast.newTextElement();
		t.setText(node.getText());
		result = (T) t;
		return false;
	}

	public boolean visit(ThisExpression node) {
		return visitNode(node);
	}

	public boolean visit(ThrowStatement node) {
		return visitNode(node);
	}

	public boolean visit(TryStatement node) {
		return visitNode(node);
	}

	public boolean visit(TypeDeclaration node) {
		return visitNode(node);
	}

	public boolean visit(TypeDeclarationStatement node) {
		return visitNode(node);
	}

	public boolean visit(TypeLiteral node) {
		return visitNode(node);
	}

	public boolean visit(TypeParameter node) {
		return visitNode(node);
	}

	public boolean visit(UnionType node) {
		return visitNode(node);
	}

	public boolean visit(VariableDeclarationExpression node) {
		return visitNode(node);
	}

	public boolean visit(VariableDeclarationFragment node) {
		return visitNode(node);
	}

	public boolean visit(VariableDeclarationStatement node) {
		return visitNode(node);
	}

	public boolean visit(WhileStatement node) {
		return visitNode(node);
	}

	public boolean visit(WildcardType node) {
		return visitNode(node);
	}

	protected boolean visitNode(ASTNode node) {
		LogUtil.logWarn(getClass().getSimpleName() + " unsupported: " + node.getClass());
		return true;
	}
}
