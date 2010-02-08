/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.test.osgi.jbossas.jbosgi36.mbean;

// $Id: FooMBean.java 86968 2009-04-08 15:51:12Z thomas.diesler@jboss.com $

import javax.management.ObjectName;

import org.jboss.osgi.spi.management.ObjectNameFactory;

public interface FooMBean 
{
   ObjectName OBJECT_NAME = ObjectNameFactory.create("jboss.osgi:test=jbosgi36");

   String echo(String msg);
   
   String accessSomeService() throws ClassNotFoundException;
   
   String accessSomeInternal() throws ClassNotFoundException;
}
