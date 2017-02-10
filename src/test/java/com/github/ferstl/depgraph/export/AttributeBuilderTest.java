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
package com.github.ferstl.depgraph.dot;

import org.junit.Test;
import com.github.ferstl.depgraph.export.AttributeBuilder;
import static org.junit.Assert.assertEquals;

/**
 * JUnit tests for {@link AttributeBuilder}.
 */
public class AttributeBuilderTest {


  @Test
  public void empty() {
    assertEquals("", new AttributeBuilder().toString());
  }

  @Test
  public void label() {
    assertEquals("[label=\"someLabel\"]", new AttributeBuilder().label("someLabel").toString());
  }

  @Test
  public void htmlLabel() {
    String label = new AttributeBuilder().label("<<b>text1\ntext2</b><font point-size=\"10\">text3</font>>").toString();
    assertEquals("[label=<<b>text1\ntext2</b><font point-size=\"10\">text3</font>>]", label);
  }

  @Test
  public void fontName() {
    assertEquals("[fontname=\"Helvetica\"]", new AttributeBuilder().fontName("Helvetica").toString());
  }

  @Test
  public void fontSize() {
    assertEquals("[fontsize=\"12\"]", new AttributeBuilder().fontSize(12).toString());
  }

  @Test
  public void fontSizeZero() {
    assertEquals("", new AttributeBuilder().fontSize(0).toString());
  }

  @Test
  public void fontSizeNull() {
    assertEquals("", new AttributeBuilder().fontSize(null).toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void fontSizeNegative() {
    new AttributeBuilder().fontSize(-1).toString();
  }

  @Test
  public void fontColor() {
    assertEquals("[fontcolor=\"green\"]", new AttributeBuilder().fontColor("green").toString());
  }

  @Test
  public void style() {
    assertEquals("[style=\"dashed\"]", new AttributeBuilder().style("dashed").toString());
  }

  @Test
  public void color() {
    assertEquals("[color=\"blue\"]", new AttributeBuilder().color("blue").toString());
  }

  @Test
  public void fillColor() {
    assertEquals("[fillcolor=\"red\"]", new AttributeBuilder().fillColor("red").toString());
  }

  @Test
  public void shape() {
    assertEquals("[shape=\"box\"]", new AttributeBuilder().shape("box").toString());
  }

  @Test
  public void addAttribute() {
    assertEquals("[someAttribute=\"someValue\"]", new AttributeBuilder().addAttribute("someAttribute", "someValue").toString());
  }

  @Test
  public void addAttributeWithNullValue() {
    assertEquals("", new AttributeBuilder().addAttribute("someAttribute", null).toString());
  }

  @Test
  public void multipleAttributes() {
    assertEquals("[label=\"someLabel\",color=\"green\",fontsize=\"10\"]", new AttributeBuilder().label("someLabel").color("green").fontSize(10).toString());
  }

  @Test
  public void quoting() {
    assertEquals("[label=\"some Label\"]", new AttributeBuilder().label("some Label").toString());
  }

  @Test
  public void escaping() {
    assertEquals("[label=\"some\\nLabel\"]", new AttributeBuilder().label("some\nLabel").toString());
  }

}
