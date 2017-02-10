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

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import com.github.ferstl.depgraph.export.DotBuilder;
import com.github.ferstl.depgraph.graph.AggregatingGraphFactory;
import com.github.ferstl.depgraph.graph.DependencyEdgeAttributeRenderer;
import com.github.ferstl.depgraph.graph.DependencyNodeAttributeRenderer;
import com.github.ferstl.depgraph.graph.GraphBuilderAdapter;
import com.github.ferstl.depgraph.graph.GraphFactory;
import com.github.ferstl.depgraph.graph.GraphNode;
import com.github.ferstl.depgraph.graph.NodeNameRenderers;
import com.github.ferstl.depgraph.graph.style.StyleConfiguration;

/**
 * Aggregates all dependencies of a multi-module project into one single graph.
 */
@Mojo(
    name = "aggregate",
    aggregator = true,
    defaultPhase = LifecyclePhase.NONE,
    inheritByDefault = false,
    requiresDependencyCollection = ResolutionScope.TEST,
    requiresDirectInvocation = false,
    threadSafe = true)
public class AggregatingDependencyGraphMojo extends AbstractAggregatingGraphMojo {

  /**
   * If set to {@code true}, the created graph will show the {@code groupId} on all artifacts.
   *
   * @since 1.0.3
   */
  @Parameter(property = "showGroupIds", defaultValue = "false")
  boolean showGroupIds;

  /**
   * If set to {@code true} the artifact nodes will show version information.
   *
   * @since 1.0.0
   */
  @Parameter(property = "showVersions", defaultValue = "false")
  boolean showVersions;

  /**
   * If set to {@code true}, all parent modules (&lt;packaging&gt;pom&lt;/packaging&gt) will be shown with a dotted
   * arrow pointing to their child modules.
   *
   * @since 1.0.0
   */
  @Parameter(property = "includeParentProjects", defaultValue = "false")
  private boolean includeParentProjects;

  @Override
  protected GraphFactory createGraphFactory(ArtifactFilter globalFilter, ArtifactFilter targetFilter, StyleConfiguration styleConfiguration) {
    DotBuilder<GraphNode> dotBuilder = new DotBuilder<>();
    dotBuilder.useNodeAttributeRenderer(new DependencyNodeAttributeRenderer(this.showGroupIds, true, this.showVersions, styleConfiguration))
        .nodeStyle(styleConfiguration.defaultNodeAttributes())
        .edgeStyle(styleConfiguration.defaultEdgeAttributes());
    if (this.mergeScopes) {
      dotBuilder.useNodeNameRenderer(NodeNameRenderers.VERSIONLESS_ID);
    } else {
      dotBuilder.useNodeNameRenderer(NodeNameRenderers.VERSIONLESS_ID_WITH_SCOPE);
    }

    // This graph won't show any conflicting dependencies. So showVersions must always be false
    dotBuilder.useEdgeAttributeRenderer(new DependencyEdgeAttributeRenderer(false, styleConfiguration));

    GraphBuilderAdapter adapter = new GraphBuilderAdapter(this.dependencyGraphBuilder, targetFilter);
    return new AggregatingGraphFactory(adapter, globalFilter, dotBuilder, this.includeParentProjects);
  }
}
