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
package org.jboss.test.osgi.jbosgi108;

//$Id: OSGI39TestCase.java 87103 2009-04-09 22:18:31Z thomas.diesler@jboss.com $

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jboss.osgi.jmx.JMXCapability;
import org.jboss.osgi.spi.management.MBeanProxy;
import org.jboss.osgi.testing.OSGiBundle;
import org.jboss.osgi.testing.OSGiPackageAdmin;
import org.jboss.osgi.testing.OSGiRuntime;
import org.jboss.osgi.testing.OSGiTestHelper;
import org.jboss.test.osgi.jbosgi108.bundleA.SomeBeanMBean;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.BundleException;

/**
 * [JBOSGI-108] Investigate statics on PackageAdmin.refresh
 * 
 * https://jira.jboss.org/jira/browse/JBOSGI-108
 * 
 * @author thomas.diesler@jboss.com
 * @since 19-Jun-2009
 */
public class OSGi108TestCase
{
   private static OSGiRuntime runtime;

   @BeforeClass
   public static void beforeClass() throws Exception
   {
      runtime = new OSGiTestHelper().getDefaultRuntime();
      runtime.addCapability(new JMXCapability());
   }

   @AfterClass
   public static void afterClass() throws BundleException
   {
      if (runtime != null)
      {
         runtime.shutdown();
         runtime = null;
      }
   }

   @Before
   public void setUp()
   {
      OSGiPackageAdmin packageAdmin = runtime.getPackageAdmin();
      packageAdmin.refreshPackages(null);
   }

   @Test
   public void testRedeploySingle() throws Exception
   {
      OSGiBundle bundleA = runtime.installBundle("jbosgi108-bundleA.jar");

      bundleA.start();

      SomeBeanMBean someBean = MBeanProxy.get(SomeBeanMBean.class, SomeBeanMBean.MBEAN_NAME, runtime.getMBeanServer());
      List<String> messages = report(someBean.getMessages());
      assertEquals("Start messages", 1, messages.size());

      bundleA.uninstall();

      // Reinstall bundleA
      bundleA = runtime.installBundle("jbosgi108-bundleA.jar");
      bundleA.start();

      // The static in bundleA.SomeBean is expected to be recreated

      messages = report(someBean.getMessages());
      assertEquals("Start messages", 1, messages.size());

      bundleA.uninstall();
   }

   @Test
   public void testRedeployWithReference() throws Exception
   {
      OSGiBundle bundleA = runtime.installBundle("jbosgi108-bundleA.jar");
      OSGiBundle bundleB = runtime.installBundle("jbosgi108-bundleB.jar");

      bundleA.start();
      bundleB.start();

      SomeBeanMBean someBean = MBeanProxy.get(SomeBeanMBean.class, SomeBeanMBean.MBEAN_NAME, runtime.getMBeanServer());
      List<String> messages = report(someBean.getMessages());
      assertEquals("Start messages", 2, messages.size());

      bundleA.uninstall();

      // After uninstall bundleA, bundleB still holds a reference on
      // bundleA.SomeBean

      // Reinstall bundleA
      bundleA = runtime.installBundle("jbosgi108-bundleA.jar");
      bundleA.start();

      // The static in bundleA.SomeBean is expected to be reused

      messages = report(someBean.getMessages());
      assertEquals("Start messages", 4, messages.size());

      bundleB.uninstall();
      bundleA.uninstall();
   }

   @Test
   public void testRedeployWithReferenceAndRefresh() throws Exception
   {
      OSGiBundle bundleA = runtime.installBundle("jbosgi108-bundleA.jar");
      OSGiBundle bundleB = runtime.installBundle("jbosgi108-bundleB.jar");

      bundleA.start();
      bundleB.start();

      SomeBeanMBean someBean = MBeanProxy.get(SomeBeanMBean.class, SomeBeanMBean.MBEAN_NAME, runtime.getMBeanServer());
      List<String> messages = report(someBean.getMessages());
      assertEquals("Start messages", 2, messages.size());

      bundleA.uninstall();

      // After uninstall bundleA, bundleB still holds a reference on
      // bundleA.SomeBean

      // Refresh all packages
      OSGiPackageAdmin packageAdmin = runtime.getPackageAdmin();
      packageAdmin.refreshPackages(null);

      // Reinstall bundleA
      bundleA = runtime.installBundle("jbosgi108-bundleA.jar");
      bundleA.start();

      // The static in bundleA.SomeBean is expected to be recreated

      messages = report(someBean.getMessages());
      assertEquals("Start messages", 1, messages.size());

      bundleB.uninstall();
      bundleA.uninstall();
   }

   private List<String> report(List<String> messages)
   {
      // System.out.println(">>>>>>>>>>>>");
      // for (String aux : messages)
      //    System.out.println(aux);
      // System.out.println("<<<<<<<<<<<");
      
      return messages;
   }
}