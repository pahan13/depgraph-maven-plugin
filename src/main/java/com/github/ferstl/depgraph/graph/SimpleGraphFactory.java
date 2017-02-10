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
package com.github.ferstl.depgraph.graph;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;

/**
 * A graph factory that creates a graph from a non multi-module project.
 */
public class SimpleGraphFactory implements GraphFactory {

  private final GraphBuilderAdapter graphBuilderAdapter;
  private final ArtifactFilter globalFilter;
  private final GraphBuilder<GraphNode> dotBuilder;

  public SimpleGraphFactory(GraphBuilderAdapter graphBuilderAdapter, ArtifactFilter globalFilter, GraphBuilder<GraphNode> dotBuilder) {
    this.graphBuilderAdapter = graphBuilderAdapter;
    this.globalFilter = globalFilter;
    this.dotBuilder = dotBuilder;
  }

  @Override
  public String createGraph(MavenProject project) {
    this.dotBuilder.graphName(project.getArtifactId());
    this.graphBuilderAdapter.buildDependencyGraph(project, this.globalFilter, this.dotBuilder);
    return this.dotBuilder.toString();
  }

}
