package org.pentaho.di.ui.repository.repositoryexplorer.abs.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.di.repository.AbsRoleInfo;
import org.pentaho.di.repository.IRole;

public class UIAbsRepositoryRoleTest {
  public final static String CREATE_CONTENT = "org.pentaho.di.creator"; //$NON-NLS-1$
  public final static String READ_CONTENT = "org.pentaho.di.reader";//$NON-NLS-1$
  public final static String ADMINISTER_SECURITY = "org.pentaho.di.securityAdministrator";//$NON-NLS-1$

  @Before
  public void init() {   
    
  }
  @Test
  public void testUIAbsRepositoryRole()  throws Exception {
    IRole role = new AbsRoleInfo();
    role.setDescription("role description");
    role.setName("myrole");
    UIAbsRepositoryRole uiRole = new UIAbsRepositoryRole(role);
    Assert.assertEquals(uiRole.getLogicalRoles().size(), 0);
    uiRole.addLogicalRole(CREATE_CONTENT);
    Assert.assertEquals(uiRole.getLogicalRoles().size(), 1);
    uiRole.removeLogicalRole(CREATE_CONTENT);
    Assert.assertEquals(uiRole.getLogicalRoles().size(), 0);
    List<String> logicalRoles = new ArrayList<String>();
    logicalRoles.add(CREATE_CONTENT);
    logicalRoles.add(READ_CONTENT);
    logicalRoles.add(ADMINISTER_SECURITY);
    uiRole.setLogicalRoles(logicalRoles);
    Assert.assertEquals(uiRole.getLogicalRoles().size(), 3);
  }
}