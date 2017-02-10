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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.AndArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.ScopeArtifactFilter;
import org.apache.maven.shared.artifact.filter.StrictPatternExcludesArtifactFilter;
import org.apache.maven.shared.artifact.filter.StrictPatternIncludesArtifactFilter;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.CommandLineUtils.StringStreamConsumer;
import org.codehaus.plexus.util.cli.Commandline;
import com.github.ferstl.depgraph.dot.DotBuilder;
import com.github.ferstl.depgraph.gml.GmlBuilder;
import com.github.ferstl.depgraph.graph.DependencyGraphException;
import com.github.ferstl.depgraph.graph.GraphBuilder;
import com.github.ferstl.depgraph.graph.GraphFactory;
import com.github.ferstl.depgraph.graph.GraphNode;
import com.github.ferstl.depgraph.graph.style.StyleConfiguration;
import com.github.ferstl.depgraph.graph.style.resource.BuiltInStyleResource;
import com.github.ferstl.depgraph.graph.style.resource.ClasspathStyleResource;
import com.github.ferstl.depgraph.graph.style.resource.FileSystemStyleResource;
import com.github.ferstl.depgraph.graph.style.resource.StyleResource;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * Abstract mojo to create all possible kinds of graphs in the dot format. Graphs are created with instances of the
 * {@link GraphFactory} interface. This class defines an abstract method to create such factories. In case Graphviz is
 * install on the system where this plugin is executed, it is also possible to run the dot program and create images
 * out of the generated dot files. Besides that, this class allows the configuration of several basic mojo parameters,
 * such as includes, excludes, etc.
 */
abstract class AbstractGraphMojo extends AbstractMojo {

  private static final Pattern LINE_SEPARATOR_PATTERN = Pattern.compile("\r?\n");
  private static final String DOT_EXTENSION = ".dot";
  private static final String OUTPUT_DOT_FILE_NAME = "dependency-graph" + DOT_EXTENSION;

  /**
   * The scope of the artifacts that should be included in the graph. An empty string indicates all scopes (default).
   * The scopes being interpreted are the scopes as Maven sees them, not as specified in the pom. In summary:
   * <ul>
   * <li>{@code compile}: Shows compile, provided and system dependencies</li>
   * <li>{@code provided}: Shows provided dependencies</li>
   * <li>{@code runtime}: Shows compile and runtime dependencies</li>
   * <li>{@code system}: Shows system dependencies</li>
   * <li>{@code test} (default): Shows all dependencies</li>
   * </ul>
   *
   * @since 1.0.0
   */
  @Parameter(property = "scope")
  private String scope;

  /**
   * List of artifacts to be included in the form of {@code groupId:artifactId:type:classifier}.
   *
   * @since 1.0.0
   */
  @Parameter(property = "includes", defaultValue = "")
  private List<String> includes;

  /**
   * List of artifacts to be excluded in the form of {@code groupId:artifactId:type:classifier}.
   *
   * @since 1.0.0
   */
  @Parameter(property = "excludes", defaultValue = "")
  private List<String> excludes;

  /**
   * List of artifacts, in the form of {@code groupId:artifactId:type:classifier}, to restrict the dependency graph
   * only to artifacts that depend on them.
   *
   * @since 1.0.4
   */
  @Parameter(property = "targetIncludes", defaultValue = "")
  private List<String> targetIncludes;
  
  /**
   * Format of graph file. Currently supported only DOT and GML formats.
   *
   * @since 2.0.2
   */
  @Parameter(property = "outputFormat", defaultValue = "DOT")
  private OutputFormat outputFormat;

  /**
   * The path to the generated dot file.
   *
   * @since 1.0.0
   */
  @Parameter(property = "outputFile", defaultValue = "${project.build.directory}/" + OUTPUT_DOT_FILE_NAME)
  private File outputFile;

  /**
   * If set to {@code true} and Graphviz is installed on the system where this plugin is executed, the dot file will be
   * converted to a graph image using Graphviz' dot executable.
   *
   * @see #imageFormat
   * @see #dotExecutable
   * @since 1.0.0
   */
  @Parameter(property = "createImage", defaultValue = "false")
  private boolean createImage;

  /**
   * The format for the graph image when {@link #createImage} is set to {@code true}.
   *
   * @since 1.0.0
   */
  @Parameter(property = "imageFormat", defaultValue = "png")
  private String imageFormat;

  /**
   * Path to the dot executable. Use this option in case {@link #createImage} is set to {@code true} and the dot
   * executable is not on the system {@code PATH}.
   *
   * @since 1.0.0
   */
  @Parameter(property = "dotExecutable")
  private File dotExecutable;

  /**
   * Path to a custom style configuration in JSON format.
   *
   * @since 2.0.0
   */
  @Parameter(property = "customStyleConfiguration", defaultValue = "")
  private String customStyleConfiguration;

  /**
   * If set to {@code true} the effective style configuration used to create this graph will be printed on the console.
   *
   * @since 2.0.0
   */
  @Parameter(property = "printStyleConfiguration", defaultValue = "false")
  private boolean printStyleConfiguration;

  /**
   * Local maven repository required by the {@link DependencyTreeBuilder}.
   */
  @Parameter(defaultValue = "${localRepository}", readonly = true)
  ArtifactRepository localRepository;

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Component(hint = "default")
  DependencyGraphBuilder dependencyGraphBuilder;

  @Component
  DependencyTreeBuilder dependencyTreeBuilder;

  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    ArtifactFilter globalFilter = createGlobalArtifactFilter();
    ArtifactFilter targetFilter = createTargetArtifactFilter();
    StyleConfiguration styleConfiguration = loadStyleConfiguration();

    try {
      GraphFactory graphFactory = createGraphFactory(globalFilter, targetFilter, styleConfiguration);

      writeDotFile(graphFactory.createGraph(this.project));

      if (this.createImage) {
        createGraphImage();
      }

    } catch (DependencyGraphException e) {
      throw new MojoExecutionException("Unable to create dependency graph.", e.getCause());
    } catch (IOException e) {
      throw new MojoExecutionException("Unable to write graph file.", e);
    }
  }

  protected abstract GraphFactory createGraphFactory(ArtifactFilter globalFilter, ArtifactFilter targetFilter, StyleConfiguration styleConfiguration);
  
  protected GraphBuilder<GraphNode> createGraphBuilder(){
    switch (outputFormat) {
      case DOT:
        return new DotBuilder<>();    
      case GML:
        return new GmlBuilder();    
      default:
        //TODO learn how should I handle such situations
        throw new RuntimeException("This exception must not happend. But unknown outputFormat happened.");
    }
  }

  /**
   * Override this method to configure additional style resources. It is recommendet to call
   * {@code super.getAdditionalStyleResources()} and add them to the set.
   *
   * @return A set of additional built-in style resources to use.
   */
  protected Set<BuiltInStyleResource> getAdditionalStyleResources() {
    // We need to preserve the order of style configurations
    return new LinkedHashSet<>();
  }

  private ArtifactFilter createGlobalArtifactFilter() {
    AndArtifactFilter filter = new AndArtifactFilter();

    if (this.scope != null) {
      filter.add(new ScopeArtifactFilter(this.scope));
    }

    if (!this.includes.isEmpty()) {
      filter.add(new StrictPatternIncludesArtifactFilter(this.includes));
    }

    if (!this.excludes.isEmpty()) {
      filter.add(new StrictPatternExcludesArtifactFilter(this.excludes));
    }

    return filter;
  }

  private ArtifactFilter createTargetArtifactFilter() {
    AndArtifactFilter filter = new AndArtifactFilter();

    if (!this.targetIncludes.isEmpty()) {
      filter.add(new StrictPatternIncludesArtifactFilter(this.targetIncludes));
    }

    return filter;
  }

  private StyleConfiguration loadStyleConfiguration() throws MojoFailureException {
    // default style resources
    ClasspathStyleResource defaultStyleResource = BuiltInStyleResource.DEFAULT_STYLE.createStyleResource(getClass().getClassLoader());

    // additional style resources from the mojo
    Set<StyleResource> styleResources = new LinkedHashSet<>();
    for (BuiltInStyleResource additionalResource : getAdditionalStyleResources()) {
      styleResources.add(additionalResource.createStyleResource(getClass().getClassLoader()));
    }

    // custom style resource
    if (StringUtils.isNotBlank(this.customStyleConfiguration)) {
      StyleResource customStyleResource = getCustomStyleResource();
      getLog().info("Using custom style configuration " + customStyleResource);
      styleResources.add(customStyleResource);
    }

    // load and print
    StyleConfiguration styleConfiguration = StyleConfiguration.load(defaultStyleResource, styleResources.toArray(new StyleResource[0]));
    if (this.printStyleConfiguration) {
      getLog().info("Using effective style configuration:\n" + styleConfiguration.toJson());
    }

    return styleConfiguration;
  }

  private StyleResource getCustomStyleResource() throws MojoFailureException {
    StyleResource customStyleResource;
    if (StringUtils.startsWith(this.customStyleConfiguration, "classpath:")) {
      String resourceName = StringUtils.substring(this.customStyleConfiguration, 10, this.customStyleConfiguration.length());
      customStyleResource = new ClasspathStyleResource(resourceName, getClass().getClassLoader());
    } else {
      customStyleResource = new FileSystemStyleResource(Paths.get(this.customStyleConfiguration));
    }

    if (!customStyleResource.exists()) {
      throw new MojoFailureException("Custom configuration '" + this.customStyleConfiguration + "' does not exist.");
    }

    return customStyleResource;
  }

  private void writeDotFile(String dotGraph) throws IOException {
    Path outputFilePath = this.outputFile.toPath();
    Path parent = outputFilePath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }

    try (Writer writer = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8)) {
      writer.write(dotGraph);
    }
  }

  private void createGraphImage() throws IOException {
    String graphFileName = createGraphFileName();
    Path graphFile = this.outputFile.toPath().getParent().resolve(graphFileName);

    String dotExecutable = determineDotExecutable();
    String[] arguments = new String[]{
        "-T", this.imageFormat,
        "-o", graphFile.toAbsolutePath().toString(),
        this.outputFile.getAbsolutePath()};

    Commandline cmd = new Commandline();
    cmd.setExecutable(dotExecutable);
    cmd.addArguments(arguments);

    getLog().info("Running Graphviz: " + dotExecutable + " " + Joiner.on(" ").join(arguments));

    StringStreamConsumer systemOut = new StringStreamConsumer();
    StringStreamConsumer systemErr = new StringStreamConsumer();
    int exitCode;

    try {
      exitCode = CommandLineUtils.executeCommandLine(cmd, systemOut, systemErr);
    } catch (CommandLineException e) {
      throw new IOException("Unable to execute Graphviz", e);
    }

    Splitter lineSplitter = Splitter.on(LINE_SEPARATOR_PATTERN).omitEmptyStrings().trimResults();
    Iterable<String> output = Iterables.concat(
        lineSplitter.split(systemOut.getOutput()),
        lineSplitter.split(systemErr.getOutput()));

    for (String line : output) {
      getLog().info("  dot> " + line);
    }

    if (exitCode != 0) {
      throw new IOException("Graphviz terminated abnormally. Exit code: " + exitCode);
    }

    getLog().info("Graph image created on " + graphFile.toAbsolutePath());
  }

  private String createGraphFileName() {
    String dotFileName = this.outputFile.getName();

    String graphFileName;
    if (dotFileName.endsWith(DOT_EXTENSION)) {
      graphFileName = dotFileName.substring(0, dotFileName.lastIndexOf(".")) + "." + this.imageFormat;
    } else {
      graphFileName = dotFileName + this.imageFormat;
    }
    return graphFileName;
  }

  private String determineDotExecutable() throws IOException {
    if (this.dotExecutable == null) {
      return "dot";
    }

    Path dotExecutablePath = this.dotExecutable.toPath();
    if (!Files.exists(dotExecutablePath)) {
      throw new NoSuchFileException("The dot executable '" + this.dotExecutable + "' does not exist.");
    } else if (Files.isDirectory(dotExecutablePath) || !Files.isExecutable(dotExecutablePath)) {
      throw new IOException("The dot executable '" + this.dotExecutable + "' is not a file or cannot be executed.");
    }

    return dotExecutablePath.toAbsolutePath().toString();
  }
  
  enum OutputFormat{
    DOT,GML
  }
}
