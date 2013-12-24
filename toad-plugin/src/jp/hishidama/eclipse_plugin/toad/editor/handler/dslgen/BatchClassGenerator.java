package jp.hishidama.eclipse_plugin.toad.editor.handler.dslgen;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;

import jp.hishidama.eclipse_plugin.toad.model.connection.Connection;
import jp.hishidama.eclipse_plugin.toad.model.diagram.Diagram;
import jp.hishidama.eclipse_plugin.toad.model.node.NodeElement;
import jp.hishidama.eclipse_plugin.toad.model.node.jobflow.JobNode;
import jp.hishidama.eclipse_plugin.util.StringUtil;

public class BatchClassGenerator extends DiagramDslClassGenerator {

	public BatchClassGenerator(IProject project, Diagram diagram) {
		super(project, diagram);
	}

	@Override
	protected void initialize() {
		if (StringUtil.isEmpty(diagramName)) {
			throw new IllegalStateException(MessageFormat.format("name={0}", diagramName));
		}
	}

	@Override
	protected void defaultImport() {
		getCachedClassName("com.asakusafw.vocabulary.batch.Batch");
		getCachedClassName("com.asakusafw.vocabulary.batch.BatchDescription");
	}

	@Override
	protected void appendClassAnnotation(StringBuilder sb) {
		sb.append("@Batch(name = \"");
		sb.append(diagramName);
		sb.append("\")\n");
	}

	@Override
	protected void appendClass(StringBuilder sb) {
		sb.append("public class ");
		sb.append(StringUtil.getSimpleName(className));
		sb.append(" extends BatchDescription {\n");
		appendDescribe(sb);
		sb.append("}\n");
	}

	private void appendDescribe(StringBuilder sb) {
		sb.append("\t@Override\n");
		sb.append("\tpublic void describe() {\n");

		List<Job> jobs = new ArrayList<Job>();
		for (NodeElement node : diagram.getContents()) {
			if (node instanceof JobNode) {
				jobs.add(new Job((JobNode) node));
			}
		}
		Map<Integer, Job> map = new HashMap<Integer, Job>();
		for (int i = 0;;) {
			if (jobs.isEmpty()) {
				break;
			}
			if (i >= jobs.size()) {
				i = 0;
			}
			Job job = jobs.get(i);
			if (job.complete(map)) {
				appendJob(sb, job, map);
				map.put(job.getId(), job);
				jobs.remove(job);
			} else {
				i++;
			}
		}

		sb.append("\t}\n");
	}

	private void appendJob(StringBuilder sb, Job job, Map<Integer, Job> map) {
		sb.append("\t\t");
		sb.append("// ");
		sb.append(job.getName());
		sb.append("\t");
		sb.append(job.getDescription());
		sb.append("\n");

		sb.append("\t\t");
		if (!job.isOutgoingsEmpty()) {
			sb.append(getCachedClassName("com.asakusafw.vocabulary.batch.Work"));
			sb.append(" ");
			sb.append(job.getVariableName());
			sb.append(" = ");
		}
		sb.append("run(");
		sb.append(getCachedClassName(job.getClassName()));
		sb.append(".class)");

		if (job.getIncomings().isEmpty()) {
			sb.append(".soon();\n");
		} else {
			sb.append(".after(");
			boolean first = true;
			for (JobNode source : job.getIncomings()) {
				Job s = map.get(source.getId());
				if (!first) {
					sb.append(", ");
				} else {
					first = false;
				}
				sb.append(s.getVariableName());
			}
			sb.append(");\n");
		}
	}

	private class Job {
		private JobNode job;
		private List<JobNode> incomings = new ArrayList<JobNode>();
		private String variableName;

		public Job(JobNode job) {
			this.job = job;
			for (Connection c : job.getIncomings()) {
				NodeElement source = c.getSource();
				if (source instanceof JobNode) {
					incomings.add((JobNode) source);
				}
			}
		}

		public Integer getId() {
			return job.getId();
		}

		public String getClassName() {
			String name = job.getClassName();
			if (StringUtil.isEmpty(name)) {
				return "undefined_className_" + getId();
			} else {
				return name;
			}
		}

		public String getName() {
			return job.getName();
		}

		public String getDescription() {
			return job.getDescription();
		}

		public boolean complete(Map<Integer, Job> map) {
			for (JobNode job : incomings) {
				if (!map.containsKey(job.getId())) {
					return false;
				}
			}
			return true;
		}

		public List<JobNode> getIncomings() {
			return incomings;
		}

		public boolean isOutgoingsEmpty() {
			for (Connection c : job.getOutgoings()) {
				NodeElement target = c.getTarget();
				if (target instanceof JobNode) {
					return false;
				}
			}
			return true;
		}

		public String getVariableName() {
			if (variableName == null) {
				String name = job.getClassName();
				if (StringUtil.isEmpty(name)) {
					name = "job";
				} else {
					name = StringUtil.getSimpleName(name);
					name = StringUtil.toFirstLower(name);
				}
				variableName = getIdentifiedName(name);
			}
			return variableName;
		}
	}
}
