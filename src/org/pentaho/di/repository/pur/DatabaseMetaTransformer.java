package org.pentaho.di.repository.pur;

import java.util.Enumeration;
import java.util.Properties;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.platform.repository.pcr.data.node.DataNode;
import org.pentaho.platform.repository.pcr.data.node.DataProperty;

import com.pentaho.commons.dsc.PentahoDscContent;
import com.pentaho.commons.dsc.PentahoLicenseVerifier;
import com.pentaho.commons.dsc.params.KParam;

public class DatabaseMetaTransformer extends AbstractDelegate implements ITransformer {

  // ~ Static fields/initializers ======================================================================================

  private static final String PROP_INDEX_TBS = "INDEX_TBS"; //$NON-NLS-1$

  private static final String PROP_DATA_TBS = "DATA_TBS"; //$NON-NLS-1$

  private static final String PROP_SERVERNAME = "SERVERNAME"; //$NON-NLS-1$

  private static final String PROP_PASSWORD = "PASSWORD"; //$NON-NLS-1$

  private static final String PROP_USERNAME = "USERNAME"; //$NON-NLS-1$

  private static final String PROP_PORT = "PORT"; //$NON-NLS-1$

  private static final String PROP_DATABASE_NAME = "DATABASE_NAME"; //$NON-NLS-1$

  private static final String PROP_HOST_NAME = "HOST_NAME"; //$NON-NLS-1$

  private static final String PROP_CONTYPE = "CONTYPE"; //$NON-NLS-1$

  private static final String PROP_TYPE = "TYPE"; //$NON-NLS-1$

  private static final String NODE_ROOT = "databaseMeta"; //$NON-NLS-1$

  private static final String NODE_ATTRIBUTES = "attributes"; //$NON-NLS-1$

  private Repository repo;

  // ~ Instance fields =================================================================================================

  // ~ Constructors ====================================================================================================

  public DatabaseMetaTransformer(final Repository repo) {
    super();
    this.repo = repo;
  }

  // ~ Methods =========================================================================================================

  public DataNode elementToDataNode(final RepositoryElementInterface element) throws KettleException {
    DatabaseMeta databaseMeta = (DatabaseMeta) element;
    DataNode rootNode = new DataNode(NODE_ROOT);

    // Then the basic db information
    //
    PentahoDscContent dscContent = PentahoLicenseVerifier.verify(new KParam());

    rootNode.setProperty(PROP_NAME, databaseMeta.getName());
    rootNode.setProperty(PROP_TYPE, DatabaseMeta.getDatabaseTypeCode(databaseMeta.getDatabaseType()));
    rootNode.setProperty(PROP_CONTYPE, DatabaseMeta.getAccessTypeDesc(databaseMeta.getAccessType()));
    rootNode.setProperty(PROP_HOST_NAME, databaseMeta.getHostname());
    rootNode.setProperty(PROP_DATABASE_NAME, databaseMeta.getDatabaseName());
    rootNode.setProperty(PROP_PORT, new Long(Const.toInt(databaseMeta.getDatabasePortNumberString(), -1)));
    rootNode.setProperty(PROP_USERNAME, databaseMeta.getUsername());
    rootNode.setProperty(PROP_PASSWORD, databaseMeta.getPassword());
    rootNode.setProperty(PROP_SERVERNAME, databaseMeta.getServername());
    rootNode.setProperty(PROP_DATA_TBS, databaseMeta.getDataTablespace());
    rootNode.setProperty(PROP_INDEX_TBS, databaseMeta.getIndexTablespace());

    DataNode attrNode = rootNode.addNode(NODE_ATTRIBUTES);

    if (dscContent.getSubject() != null) {

      // Now store all the attributes set on the database connection...
      // 
      Properties attributes = databaseMeta.getAttributes();
      Enumeration<Object> keys = databaseMeta.getAttributes().keys();
      while (keys.hasMoreElements()) {
        String code = (String) keys.nextElement();
        String attribute = (String) attributes.get(code);

        // Save this attribute
        //
        attrNode.setProperty(code, attribute);
      }

    }
    return rootNode;

  }

  public RepositoryElementInterface dataNodeToElement(final DataNode rootNode) throws KettleException {
    DatabaseMeta databaseMeta = new DatabaseMeta();
    dataNodeToElement(rootNode, databaseMeta);
    return databaseMeta;
  }
  
  public void dataNodeToElement(final DataNode rootNode, final RepositoryElementInterface element) throws KettleException {
    PentahoDscContent dscContent = PentahoLicenseVerifier.verify(new KParam());

    DatabaseMeta databaseMeta = (DatabaseMeta) element;
    if (dscContent.getExtra() != null) {
      databaseMeta.setName(rootNode.getProperty(PROP_NAME).getString());
    }

    if (dscContent.getHolder() != null) {
      databaseMeta.setDatabaseType(rootNode.getProperty(PROP_TYPE).getString());
    }
    databaseMeta.setAccessType(DatabaseMeta.getAccessType(rootNode.getProperty(PROP_CONTYPE).getString()));
    databaseMeta.setHostname(rootNode.getProperty(PROP_HOST_NAME).getString());
    databaseMeta.setDBName(rootNode.getProperty(PROP_DATABASE_NAME).getString());
    databaseMeta.setDBPort(rootNode.getProperty(PROP_PORT).getString());
    databaseMeta.setUsername(rootNode.getProperty(PROP_USERNAME).getString());
    databaseMeta.setPassword(rootNode.getProperty(PROP_PASSWORD).getString());
    databaseMeta.setServername(rootNode.getProperty(PROP_SERVERNAME).getString());
    databaseMeta.setDataTablespace(rootNode.getProperty(PROP_DATA_TBS).getString());
    databaseMeta.setIndexTablespace(rootNode.getProperty(PROP_INDEX_TBS).getString());

    // Also, load all the properties we can find...

    DataNode attrNode = rootNode.getNode(NODE_ATTRIBUTES);
    for (DataProperty property : attrNode.getProperties()) {
      String code = property.getName();
      String attribute = property.getString();
      databaseMeta.getAttributes().put(code, Const.NVL(attribute, "")); //$NON-NLS-1$
    }
  }

}