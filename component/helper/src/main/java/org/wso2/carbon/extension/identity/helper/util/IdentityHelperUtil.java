/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.extension.identity.helper.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.util.SecurityManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.extension.identity.helper.IdentityHelperConstants;
import org.wso2.carbon.identity.application.authentication.framework.config.builder.FileBasedConfigurationBuilder;
import org.wso2.carbon.identity.application.authentication.framework.config.model.AuthenticatorConfig;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * AuthenticationFrameworkUtil class.
 */
public class IdentityHelperUtil {
    private static Log log = LogFactory.getLog(IdentityHelperUtil.class);
    private static final int ENTITY_EXPANSION_LIMIT = 0;

    /**
     * Check the helper enabled by admin.
     *
     * @param context the authentication context
     * @return true or false
     */
    public static boolean checkSecondStepEnableByAdmin(AuthenticationContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Read the values of enable second step from application authentication xml file");
        }
        String tenantDomain = context.getTenantDomain();
        if (tenantDomain.equals(IdentityHelperConstants.SUPER_TENANT_DOMAIN)) {
            return Boolean.parseBoolean(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.ENABLE_SECOND_STEP));
        } else {
            Object getPropertiesFromLocal = context
                    .getProperty(IdentityHelperConstants.GET_PROPERTY_FROM_REGISTRY);
            if (getPropertiesFromLocal == null) {
                return Boolean.parseBoolean(context.getProperty(IdentityHelperConstants.ENABLE_SECOND_STEP)
                        .toString());
            }
            return Boolean.parseBoolean(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.ENABLE_SECOND_STEP));
        }
    }

    /**
     * Get the secondary user store names.
     *
     * @param context Authentication context.
     */
    public static String getSecondaryUserStore(AuthenticationContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Read the secondary user store from application authentication xml file");
        }
        String tenantDomain = context.getTenantDomain();
        if (tenantDomain.equals(IdentityHelperConstants.SUPER_TENANT_DOMAIN)) {
            return String.valueOf(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.SECONDARY_USER_STORE));
        } else {
            Object getPropertiesFromLocal = context
                    .getProperty(IdentityHelperConstants.GET_PROPERTY_FROM_REGISTRY);
            if (getPropertiesFromLocal == null) {
                return context.getProperty(IdentityHelperConstants.SECONDARY_USER_STORE).toString();
            }
            return String.valueOf(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.SECONDARY_USER_STORE));
        }
    }

    /**
     * Get the federated helper user attribute.
     *
     * @param context Authentication context.
     * @return user attribute
     */
    public static String getUserAttribute(AuthenticationContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Read the user attribute from application authentication xml file");
        }
        String tenantDomain = context.getTenantDomain();
        if (tenantDomain.equals(IdentityHelperConstants.SUPER_TENANT_DOMAIN)) {
            return String.valueOf(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.USER_ATTRIBUTE));
        } else {
            Object getPropertiesFromLocal = context
                    .getProperty(IdentityHelperConstants.GET_PROPERTY_FROM_REGISTRY);
            if (getPropertiesFromLocal == null) {
                return context.getProperty(IdentityHelperConstants.USER_ATTRIBUTE).toString();
            }
            return String.valueOf(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.USER_ATTRIBUTE));
        }
    }

    /**
     * Get usecase type which is used to get username
     *
     * @param context Authentication context.
     * @return usecase type (local, association, userAttribute, subjectUri)
     */
    public static String getUsecase(AuthenticationContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Read the use case type from application authentication xml file");
        }
        String tenantDomain = context.getTenantDomain();
        if (tenantDomain.equals(IdentityHelperConstants.SUPER_TENANT_DOMAIN)) {
            return String.valueOf(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.USE_CASE));
        } else {
            Object getPropertiesFromLocal = context.getProperty(IdentityHelperConstants.GET_PROPERTY_FROM_REGISTRY);
            if (getPropertiesFromLocal == null) {
                return context.getProperty(IdentityHelperConstants.USE_CASE).toString();
            }
            return String.valueOf(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.USE_CASE));
        }
    }

    /**
     * Check whether multi factor authentication policy enabled.
     *
     * @param context the authentication context
     * @return isMultiFactorAuthenticationPolicyEnable value
     */
    public static boolean isMultiFactorAuthenticationPolicyEnable(AuthenticationContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Read the multiFactorAuthenticationPolicyEnable from application authentication xml file");
        }
        String tenantDomain = context.getTenantDomain();
        if (tenantDomain.equals(IdentityHelperConstants.SUPER_TENANT_DOMAIN)) {
            return Boolean.parseBoolean(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.AUTHENTICATION_POLICY_ENABLED));
        } else {
            Object getPropertiesFromLocal = context.getProperty(IdentityHelperConstants
                    .GET_PROPERTY_FROM_REGISTRY);
            if (getPropertiesFromLocal == null) {
                return Boolean.parseBoolean(context.getProperty(IdentityHelperConstants
                        .AUTHENTICATION_POLICY_ENABLED).toString());
            }
            return Boolean.parseBoolean(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.AUTHENTICATION_POLICY_ENABLED));
        }
    }

    /**
     * Check whether multi factor authentication policy account lock on failure enabled.
     *
     * @param context the authentication context
     * @return isMultiFactorAuthenticationPolicyEnable value
     */
    public static boolean isMultiFactorAuthenticationPolicyAccountLockOnFailure(AuthenticationContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Read the multi factor authentication policy account lock on failure value" +
                    " from application authentication xml file");
        }
        String tenantDomain = context.getTenantDomain();
        if (tenantDomain.equals(IdentityHelperConstants.SUPER_TENANT_DOMAIN)) {
            return Boolean.parseBoolean(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.AUTHENTICATION_POLICY_ACCOUNT_LOCK_ON_FAILURE));
        } else {
            Object getPropertiesFromLocal = context.getProperty(IdentityHelperConstants.GET_PROPERTY_FROM_REGISTRY);
            if (getPropertiesFromLocal == null) {
                return Boolean.parseBoolean(context.getProperty(IdentityHelperConstants
                        .AUTHENTICATION_POLICY_ACCOUNT_LOCK_ON_FAILURE).toString());
            }
            return Boolean.parseBoolean(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.AUTHENTICATION_POLICY_ACCOUNT_LOCK_ON_FAILURE));
        }
    }

    /**
     * Get the value of  multi factor authentication policy account lock on failure max attempts.
     *
     * @param context the authentication context
     * @return maximum applying authentication code failing attempts
     */
    public static int getMultiFactorAuthenticationPolicyAccountLockOnFailureMaxAttempts(AuthenticationContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Read the multiFactor authentication policy account lock on failure max attempts" +
                    " from application authentication xml file");
        }
        String tenantDomain = context.getTenantDomain();
        if (tenantDomain.equals(IdentityHelperConstants.SUPER_TENANT_DOMAIN)) {
            return Integer.parseInt(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.AUTHENTICATION_POLICY_ACCOUNT_LOCK_ON_FAILURE_MAS_ATTEMPTS));
        } else {
            Object getPropertiesFromLocal = context.getProperty(IdentityHelperConstants.GET_PROPERTY_FROM_REGISTRY);
            if (getPropertiesFromLocal == null) {
                return Integer.parseInt(context.getProperty(IdentityHelperConstants
                        .AUTHENTICATION_POLICY_ACCOUNT_LOCK_ON_FAILURE_MAS_ATTEMPTS).toString());
            }
            return Integer.parseInt(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.AUTHENTICATION_POLICY_ACCOUNT_LOCK_ON_FAILURE_MAS_ATTEMPTS));
        }
    }

    /**
     * Get the account lock time.
     *
     * @param context the authentication context
     * @return account lock time
     */
    public static int getMultiFactorAuthenticationPolicyAccountLockTime(AuthenticationContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Read the multi factor authentication policy account lock time from application authentication xml file");
        }
        String tenantDomain = context.getTenantDomain();
        if (tenantDomain.equals(IdentityHelperConstants.SUPER_TENANT_DOMAIN)) {
            return Integer.parseInt(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.AUTHENTICATION_POLICY_ACCOUNT_LOCK_TIME));
        } else {
            Object getPropertiesFromLocal = context.getProperty(IdentityHelperConstants.GET_PROPERTY_FROM_REGISTRY);
            if (getPropertiesFromLocal == null) {
                return Integer.parseInt(context.getProperty(IdentityHelperConstants
                        .AUTHENTICATION_POLICY_ACCOUNT_LOCK_TIME).toString());
            }
            return Integer.parseInt(getAuthenticatorParameters(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString())
                    .get(IdentityHelperConstants.AUTHENTICATION_POLICY_ACCOUNT_LOCK_TIME));
        }
    }

    /**
     * Get xml file data from registry, covert string type of xml content to xml document and save the value
     * to authentication the context.
     *
     * @param context           the authentication context
     * @param authenticatorName the authenticator name
     * @param tenantDomain      the tenant domain
     * @throws AuthenticationFailedException
     */
    public static void loadApplicationAuthenticationXMLFromRegistry(AuthenticationContext context, String authenticatorName,
                                                                    String tenantDomain) throws AuthenticationFailedException {

        int tenantID = IdentityTenantUtil.getTenantId(tenantDomain);
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            privilegedCarbonContext.setTenantId(tenantID);
            privilegedCarbonContext.setTenantDomain(tenantDomain);
            Registry registry = (Registry) privilegedCarbonContext.getRegistry(RegistryType.SYSTEM_GOVERNANCE);
            // Get the registry path.
            Resource resource = registry.get(context
                    .getProperty(IdentityHelperConstants.AUTHENTICATION).toString() + "/"
                    + IdentityHelperConstants.REGISTRY_PATH);
            Object content = resource.getContent();
            String xml = new String((byte[]) content);
            DocumentBuilderFactory factory = getSecuredDocumentBuilderFactory();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));

            NodeList authConfigList = doc.getElementsByTagName(IdentityHelperConstants.AUTHENTICATION_CONFIG);

            for (int authConfigIndex = 0; authConfigIndex < authConfigList.getLength(); authConfigIndex++) {
                Node authConfigNode = authConfigList.item(authConfigIndex);
                if (authConfigNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element authConfigElement = (Element) authConfigNode;
                    //Get the name attribute value of authenticator from  authConfigElement.
                    String AuthConfig = authConfigElement.getAttribute(IdentityHelperConstants.NAME);
                    if (AuthConfig.equals(authenticatorName)) {
                        NodeList AuthConfigChildList = authConfigElement.getChildNodes();
                        for (int j = 0; j < AuthConfigChildList.getLength(); j++) {
                            Node authConfigChildNode = AuthConfigChildList.item(j);
                            if (authConfigChildNode.getNodeType() == Node.ELEMENT_NODE) {
                                NodeList authenticatorNodeList = authConfigChildNode.getChildNodes();
                                for (int i = 0; i < authenticatorNodeList.getLength(); i++) {
                                    Node currentNode = authenticatorNodeList.item(i);
                                    context.setProperty(authConfigChildNode.getAttributes().item(i).getNodeValue(),
                                            currentNode.getNodeValue());
                                }
                            }
                        }
                        break;
                    }
                }
            }
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new AuthenticationFailedException("Cannot get the parameter values from registry ", e);
        } catch (RegistryException e) {
            context.setProperty(IdentityHelperConstants.GET_PROPERTY_FROM_REGISTRY,
                    IdentityHelperConstants.GET_PROPERTY_FROM_REGISTRY);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    /**
     * Get parameter values from local file.
     *
     * @param authenticatorName the authenticator name
     * @return parameters map
     */
    public static Map<String, String> getAuthenticatorParameters(String authenticatorName) {
        AuthenticatorConfig authConfig = FileBasedConfigurationBuilder.getInstance()
                .getAuthenticatorBean(authenticatorName);
        return authConfig.getParameterMap();
    }

    /**
     * Create DocumentBuilderFactory with the XXE and XEE prevention measurements.
     *
     * @return DocumentBuilderFactory instance
     */
    public static DocumentBuilderFactory getSecuredDocumentBuilderFactory() {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        try {
            dbf.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE, false);
            dbf.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE, false);
            dbf.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

        } catch (ParserConfigurationException e) {
            log.error("Failed to load XML Processor Feature " + Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE + " or " +
                    Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE + " or " + Constants.LOAD_EXTERNAL_DTD_FEATURE +
                    " or secure-processing.");
        }

        SecurityManager securityManager = new SecurityManager();
        securityManager.setEntityExpansionLimit(ENTITY_EXPANSION_LIMIT);
        dbf.setAttribute(Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY, securityManager);

        return dbf;

    }
}