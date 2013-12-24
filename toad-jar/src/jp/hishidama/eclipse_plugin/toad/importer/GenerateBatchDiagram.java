package jp.hishidama.eclipse_plugin.toad.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.asakusafw.vocabulary.batch.BatchDescription;
import com.asakusafw.vocabulary.batch.JobFlowWorkDescription;
import com.asakusafw.vocabulary.batch.Work;

public class GenerateBatchDiagram {

	public Map<String, List<String>> getDependencies(String className) throws Exception {

		@SuppressWarnings("unchecked")
		Class<BatchDescription> clazz = (Class<BatchDescription>) Class.forName(className);
		BatchDescription batch = clazz.newInstance();
		batch.start();
		Collection<Work> works = batch.getWorks();
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>(works.size());
		for (Work work : works) {
			String name = getName(work);

			List<Work> dependencies = work.getDependencies();
			List<String> list = new ArrayList<String>(dependencies.size());
			for (Work d : dependencies) {
				list.add(getName(d));
			}

			map.put(name, list);
		}

		return map;
	}

	private String getName(Work work) {
		JobFlowWorkDescription job = (JobFlowWorkDescription) work.getDescription();
		String name = job.getFlowClass().getCanonicalName();
		return name;
	}
}
