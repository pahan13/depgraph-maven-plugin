/*
 * Copyright (c) 2014 - 2016 by Stefan Ferstl <st.ferstl@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ferstl.depgraph;

import java.util.Set;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import com.github.ferstl.depgraph.graph.DependencyNodeAttributeRenderer;
import com.github.ferstl.depgraph.graph.GraphBuilder;
import com.github.ferstl.depgraph.graph.GraphBuilderAdapter;
import com.github.ferstl.depgraph.graph.GraphFactory;
import com.github.ferstl.depgraph.graph.GraphNode;
import com.github.ferstl.depgraph.graph.NodeNameRenderers;
import com.github.ferstl.depgraph.graph.NodeResolution;
import com.github.ferstl.depgraph.graph.SimpleGraphFactory;
import com.github.ferstl.depgraph.graph.style.StyleConfiguration;
import com.github.ferstl.depgraph.graph.style.resource.BuiltInStyleResource;
import static java.util.EnumSet.allOf;

/**
 * Creates a graph containing the group IDs of all dependencies.
 */
@Mojo(
    name = "by-groupid",
    aggregator = false,
    defaultPhase = LifecyclePhase.NONE,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class DependencyGraphByGroupIdMojo extends AbstractGraphMojo {

  @Override
  protected GraphFactory createGraphFactory(ArtifactFilter globalFilter, ArtifactFilter targetFilter, StyleConfiguration styleConfiguration) {
    GraphBuilder<GraphNode> builder = super.createGraphBuilder();

    builder
        .nodeStyle(styleConfiguration.defaultNodeAttributes())
        .edgeStyle(styleConfiguration.defaultEdgeAttributes())
        .useNodeNameRenderer(NodeNameRenderers.GROUP_ID_WITH_SCOPE)
        .useNodeAttributeRenderer(new DependencyNodeAttributeRenderer(true, false, false, styleConfiguration))
        .omitSelfReferences();

    GraphBuilderAdapter adapter = new GraphBuilderAdapter(this.dependencyTreeBuilder, this.localRepository, targetFilter, allOf(NodeResolution.class));
    return new SimpleGraphFactory(adapter, globalFilter, builder);
  }

  @Override
  protected Set<BuiltInStyleResource> getAdditionalStyleResources() {
    Set<BuiltInStyleResource> resources = super.getAdditionalStyleResources();
    resources.add(BuiltInStyleResource.GROUP_ID_ONLY_STYLE);

    return resources;
  }
}
