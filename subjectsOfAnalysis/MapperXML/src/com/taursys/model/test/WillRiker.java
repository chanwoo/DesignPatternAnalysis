/**
 * WillRiker - A test Character
 *
 * Copyright (c) 2002
 *      Marty Phelan, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.taursys.model.test;

import java.text.*;
import java.util.*;
import java.math.*;

/**
 * WillRiker is a test character
 * @author Marty Phelan
 * @version 1.0
 */
public class WillRiker extends com.taursys.model.test.TestValueObject {

  /**
   * Constructs a new WillRiker
   */
  public WillRiker() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
    this.setFullName("William T Riker");
    this.setActive(true);
    this.setBirthdate(df.parse("08/15/2003"));
    this.setCreateDate(df.parse("01/01/2000"));
    this.setDependents(0);
    this.setSalary(BigDecimal.valueOf(3000L));
  }
}
