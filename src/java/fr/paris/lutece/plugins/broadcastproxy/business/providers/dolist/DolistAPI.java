/*
 * Copyright (c) 2002-2023, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.broadcastproxy.business.providers.dolist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

public class DolistAPI
{

    // URLs
    private static final String PROPERTY_ACCOUNT_ID = AppPropertiesService.getProperty( "dolist.CONSTANTE_ACCOUNT_ID" );
    private static final String URL_BASE_API = AppPropertiesService.getProperty( "dolist.URL_PATH_BASE_API" );
    private static final String URL_CONTACTS = AppPropertiesService.getProperty( "dolist.URL_PATH_CONTACTS" );
    private static final String URL_EXISTS = AppPropertiesService.getProperty( "dolist.URL_PATH_EXISTS" );
    private static final String URL_ACCOUNT_ID = AppPropertiesService.getProperty( "dolist.URL_PATH_ACCOUNT_ID" );
    private static final String URL_SUBSRIPTIONS = AppPropertiesService.getProperty( "dolist.URL_PATH_SUBSRIPTIONS" );
    private static final String URL_INTERESTS = AppPropertiesService.getProperty( "dolist.URL_PATH_INTERESTS" );
    private static final String URL_CHANNEL = AppPropertiesService.getProperty( "dolist.URL_PATH_CHANNEL" );
    private static final String URL_GROUP_INTERESTS = AppPropertiesService.getProperty( "dolist.URL_PATH_GROUP_INTERESTS" );
    private static final String URL_OPERATION_MODE = AppPropertiesService.getProperty( "dolist.URL_PATH_OPERATION_MODE" );
    private static final String URL_CONTACT_INTEREST_ORIGIN = AppPropertiesService.getProperty( "dolist.URL_PATH_CONTACT_INTEREST_ORIGIN" );
    private static final String URL_INTERESTS_ACTIVE_ONLY = AppPropertiesService.getProperty( "dolist.URL_PATH_INTERESTS_ACTIVE_ONLY" );

    // Markers
    private static final String MARK_HEADER_CONTENT_TYPE = AppPropertiesService.getProperty( "dolist.MARK_HEADER_CONTENT_TYPE" );
    private static final String MARK_HEADER_ACCEPT = AppPropertiesService.getProperty( "dolist.MARK_HEADER_ACCEPT" );
    private static final String MARK_HEADER_ACCEPT_LANGUAGE = AppPropertiesService.getProperty( "dolist.MARK_HEADER_ACCEPT_LANGUAGE" );
    private static final String MARK_HEADER_X_API_KEY = AppPropertiesService.getProperty( "dolist.MARK_HEADER_X_API_KEY" );

    // Header constants
    private static final String CONSTANTE_HEADER_CONTENT_TYPE = AppPropertiesService.getProperty( "dolist.CONSTANTE_HEADER_CONTENT_TYPE" );
    private static final String CONSTANTE_HEADER_ACCEPT = AppPropertiesService.getProperty( "dolist.CONSTANTE_HEADER_ACCEPT" );
    private static final String CONSTANTE_HEADER_ACCEPT_LANGUAGE = AppPropertiesService.getProperty( "dolist.CONSTANTE_HEADER_ACCEPT_LANGUAGE" );
    private static final String CONSTANTE_HEADER_X_API_KEY = AppPropertiesService.getProperty( "dolist.CONSTANTE_HEADER_X_API_KEY" );
    private static final String CONSTANTE_HEADER_X_API_KEY_PREFIX = "dolist.CONSTANTE_HEADER_X_API_KEY_PARIS_";
    
    // URL parameter's constants
    private static final String CONSTANTE_CHANNEL = AppPropertiesService.getProperty( "dolist.CONSTANTE_CHANNEL" );
    private static final String CONSTANTE_EMAIL_FIELD_ID = AppPropertiesService.getProperty( "dolist.CONSTANTE_EMAIL_FIELD_ID" );
    private static final String CONSTANTE_CONTACT_INTEREST_ORIGIN = AppPropertiesService.getProperty( "dolist.CONSTANTE_CONTACT_INTEREST_ORIGIN" );
    private static final String CONSTANTE_INTERESTS_ACTIVE_ONLY = AppPropertiesService.getProperty( "dolist.CONSTANTE_INTERESTS_ACTIVE_ONLY" );

    private static final String CONSTANTE_REQUEST_BODY_CONTACT = AppPropertiesService.getProperty( "dolist.CONSTANTE_REQUEST_BODY_CONTACT" );
    private static final String CONSTANTE_REQUEST_BODY_COMPOSITE_LIST = AppPropertiesService.getProperty( "dolist.CONSTANTE_REQUEST_BODY_COMPOSITE_LIST" );
    private static final String CONSTANTE_REQUEST_BODY_SUBSCRIPTIONS_LIST = AppPropertiesService
            .getProperty( "dolist.CONSTANTE_REQUEST_BODY_SUBSCRIPTIONS_LIST" );
    private static final String CONSTANTE_REQUEST_BODY_INTERESTS_LIST = AppPropertiesService.getProperty( "dolist.CONSTANTE_REQUEST_BODY_INTERESTS_LIST" );

    // Other constants
    private static final String JSON_NODE_USER_ID = AppPropertiesService.getProperty( "dolist.jsonNode.user.ID" );
    private static final String JSON_NODE_SEARCH_NAME = AppPropertiesService.getProperty( "dolist.jsonNode.item.Name" );
    private static final String JSON_NODE_FIELD_LIST = AppPropertiesService.getProperty( "dolist.jsonNode.FieldList" );

    // Instance variables
    private String _userEmail;
    private String _contactId;
    private String _accountId;

    /**
     * get ContactID
     * 
     * @param userEmail
     * @param strAccountId
     * @return Dolist contact id
     * @throws Exception
     */
    public String getDolistContactId( String userEmail, String strAccountId ) throws Exception
    {
        if ( userEmail == null )
            return null;        

        if ( userEmail.equals( _userEmail ) && strAccountId.equals( _accountId ) )
            return _contactId;

        ObjectMapper mapper = new ObjectMapper( );
        Map<String, Object> queryParams = new HashMap<>( );
        String strResponse = null;
        String strContactId = "";

        String strUrl = URL_BASE_API + URL_CONTACTS + URL_EXISTS + "?" + URL_ACCOUNT_ID + "=" + strAccountId;

        try
        {
            // Set Headers
            Map<String, String> mapHeaders = constructHeader( strAccountId );

            // Set request parameters
            queryParams.put( JSON_NODE_SEARCH_NAME, userEmail );
            queryParams.put( JSON_NODE_USER_ID, Integer.parseInt( CONSTANTE_EMAIL_FIELD_ID ) );

            String strParamsInJson = "{ \"" + CONSTANTE_REQUEST_BODY_COMPOSITE_LIST + "\":{\"FieldValueList\":[" + mapper.writeValueAsString( queryParams ) + "]}}";

            // Call Dolist API
            strResponse = callDoPost( strUrl, strParamsInJson, mapHeaders );

            // Get ContactId from response
            JsonNode nodes = mapper.readTree( strResponse );

            if ( Integer.parseInt( nodes.get( JSON_NODE_USER_ID ).asText( ) ) > 0 )
            {
                strContactId = nodes.get( JSON_NODE_USER_ID ).asText( );
                if ( strContactId == null || strContactId.isEmpty( ) )
                {
                    throw new Exception( );
                }

                // set instance variables
                _userEmail = userEmail;
                _contactId = strContactId;
                _accountId = strAccountId;
            }
//            else if ( Integer.parseInt( nodes.get( "Count" ).asText( ) ) > 1 ) // There is some accounts with the same email
//            {
//            	String strError = "There is some accounts with the same email : '" + userEmail;
//                AppLogService.error( strError );
//                return null;
//            }
            else // There is not account for this user
            {
                return "";            	
            }

        }
        catch( Exception e )
        {
            String strError = "Error occured while getting Contact ID from '" + strUrl + "' : " + e.getMessage( );
            AppLogService.error( strError + e.getMessage( ), e );
            throw new Exception( strError );
        }

        return strContactId;
    }

    /**
     * ADD user
     * 
     * @param eMail
     * @param strAccountId
     * @throws IOException
     */
    public String addUser( String userEmail, String strAccountId ) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper( );
        Map<String, String> param = new HashMap<>( );
        List<Map<String, String>> paramsList = new ArrayList<Map<String, String>>( );
        String strResponse = null;
        String strContactId = "";

        // Set URL
        String strUrl = URL_BASE_API + URL_CONTACTS + "?" + URL_ACCOUNT_ID + "=" + strAccountId;

        try
        {
            // Set Headers
            Map<String, String> mapHeaders = constructHeader( strAccountId );

            param.put( "ID", CONSTANTE_EMAIL_FIELD_ID );
            param.put( "Value", userEmail );

            paramsList.add( param );

            String strParamsInJson = "{ \"" + CONSTANTE_REQUEST_BODY_CONTACT + "\":{\"" + JSON_NODE_FIELD_LIST + "\":" + mapper.writeValueAsString( paramsList )
                    + "}}";

            // Call Dolist API
            strResponse = callDoPost( strUrl, strParamsInJson, mapHeaders );

            // Get ContactId from response
            JsonNode nodes = mapper.readTree( strResponse );

            strContactId = nodes.get( JSON_NODE_USER_ID ).asText( );
        }
        catch( IOException | HttpAccessException e )
        {
            String strError = "Error occured while add/delete user. '" + strUrl + "' : ";
            AppLogService.error( strError + e.getMessage( ), e );
            return null;
        }

        // set instance variables
        _userEmail = userEmail;
        _contactId = strContactId;

        return strContactId;
    }

    /**
     * get subscriptions Names (label)
     * 
     * @param typeSubscription
     * @param strAccountId
     * @return json
     * @throws Exception
     */
    public String getAllSubscriptions( String typeSubscription, String strAccountId ) throws Exception
    {
        String strResponse = StringUtils.EMPTY;
        String strUrl = StringUtils.EMPTY;

        try
        {
            if ( typeSubscription.equals( DolistConstants.TYPE_SUBSCRIPTION ) )
            {
                // Get dolist newsletters ids and names (Dolist subscriptions)
                strUrl = URL_BASE_API + URL_SUBSRIPTIONS + "?" + URL_CHANNEL + "=" + CONSTANTE_CHANNEL + "&" + URL_ACCOUNT_ID + "=" + strAccountId;
            }
            else
                if ( typeSubscription.equals( DolistConstants.TYPE_INTEREST ) )
                {
                    // Get dolist alerts ids and names (Dolist interests)
                    strUrl = URL_BASE_API + URL_INTERESTS + URL_GROUP_INTERESTS + "?" + URL_ACCOUNT_ID + "=" + strAccountId + "&"
                            + URL_INTERESTS_ACTIVE_ONLY + "=" + CONSTANTE_INTERESTS_ACTIVE_ONLY;
                }

            Map<String, String> mapHeaders = constructHeader( strAccountId );

            strResponse = callDoGet( strUrl, mapHeaders );
        }
        catch( IOException e )
        {
            String strError = "Error occured while getting the list of subscriptions names '" + strUrl + "' : " + e.getMessage( );
            AppLogService.error( strError + e.getMessage( ), e );
            throw new Exception( strError );
        }

        return strResponse;
        
    }

    /**
     * get Contact's subscriptions
     * 
     * @param userEmail
     * @param strAccountId
     * @return json
     * @throws Exception
     */
    public String getUserSubscriptions( String userEmail, String typeSubscription, String strAccountId ) throws Exception
    {
        String strResponse = StringUtils.EMPTY;
        String strUrl = StringUtils.EMPTY;

        // Get contact ID
        String idContact = getDolistContactId( userEmail, strAccountId );

        if ( idContact == null )
        	return null;
        
        // if Email (user) does not exist
        if ( idContact.equals( "" ) )
        	return "";

        try
        {
            if ( typeSubscription.equals( DolistConstants.TYPE_SUBSCRIPTION ) )
            {
                // Get user newsletters (Dolist subscriptions)
                strUrl = URL_BASE_API + URL_CONTACTS + "/" + idContact + URL_SUBSRIPTIONS + "?" + URL_ACCOUNT_ID + "=" + strAccountId + "&"
                        + URL_CHANNEL + "=" + CONSTANTE_CHANNEL;
            }
            else
                if ( typeSubscription.equals( DolistConstants.TYPE_INTEREST ) )
                {
                    // Get user alerts (Dolist interests)
                    strUrl = URL_BASE_API + URL_CONTACTS + "/" + idContact + URL_INTERESTS + "?" + URL_ACCOUNT_ID + "=" + strAccountId;
                }

            Map<String, String> mapHeaders = constructHeader( strAccountId );

            strResponse = callDoGet( strUrl, mapHeaders );

        }
        catch( IOException e )
        {
            String strError = "Error occured while getting Contact subscriptions list from '" + strUrl + "' : " + e.getMessage( );
            AppLogService.error( strError + e.getMessage( ), e );
            throw new Exception( strError );
        }

        return strResponse;
        
    }

    /**
     * update subscriptions
     * 
     * @param ContactName
     * @param mapSubscriptions
     * @param typeSubscription
     * @param strAccountId
     * @return response
     * @throws Exception
     */
    public String updateSubscribtions( String userEmail, Map<String, String> subscriptionsToUpdate, String strAccountId ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );
        List<Map<String, Object>> listModifiedSubscriptions = new ArrayList<>( );

        // Get dolist contact ID
        String userDolistId = getDolistContactId( userEmail, strAccountId );

        // if Email (user) does not exist ==> Create user
        if ( userDolistId.equals( "" ) || userDolistId.equals( "0" )  )
            userDolistId = addUser( userEmail, strAccountId );

        // Set Headers
        Map<String, String> mapHeaders = constructHeader( strAccountId );

        // Update Dolist Subscriptions
        String strUrl = URL_BASE_API + URL_CONTACTS + "/" + userDolistId + URL_SUBSRIPTIONS + "?" + URL_ACCOUNT_ID + "=" + strAccountId;

        // Set request parameters
        for ( Map.Entry<String, String> subStatus : subscriptionsToUpdate.entrySet( ) )
        {
            Map<String, Object> sub = new HashMap<>( );
            sub.put( "SubscriptionID", Integer.parseInt( subStatus.getKey( ) ) );
            sub.put( "Status", subStatus.getValue( ) );
            listModifiedSubscriptions.add( sub );
        }

        String strParamsInJson = "{ \"" + CONSTANTE_REQUEST_BODY_SUBSCRIPTIONS_LIST + "\":" + mapper.writeValueAsString( listModifiedSubscriptions ) + "}";

        // Call http method (PUT)
        String response = callDoPut( strUrl, strParamsInJson, mapHeaders );

        return response;
    }

    public String updateInterests( String userEmail, List<Integer> subscriptionsToUpdate, String action, String strAccountId ) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper( );
        String response = StringUtils.EMPTY;

        // Get dolist contact ID
        String userDolistId = getDolistContactId( userEmail, strAccountId );
        
        // if Email (user) does not exist ==> Create user
        if ( userDolistId.equals( "" ) )
            userDolistId = addUser( userEmail, strAccountId );

        // Set Headers
        Map<String, String> mapHeaders = constructHeader( strAccountId );

        String strUrl = URL_BASE_API + URL_CONTACTS + "/" + userDolistId + URL_INTERESTS + "?" + URL_CONTACT_INTEREST_ORIGIN + "="
                + CONSTANTE_CONTACT_INTEREST_ORIGIN + "&" + URL_OPERATION_MODE + "=" + action + "&" + URL_ACCOUNT_ID + "=" + strAccountId;

        try
        {
            // Update Dolist Interests
            String strParamsInJson = "{\"" + CONSTANTE_REQUEST_BODY_INTERESTS_LIST + "\":" + mapper.writeValueAsString( subscriptionsToUpdate ) + "}";

            // Call http method (PUT)
            response = callDoPut( strUrl, strParamsInJson, mapHeaders );

        }
        catch( IOException e )
        {
            String strError = "Error connecting to '" + strUrl + "' : " + e.getMessage( );
            AppLogService.error( strError + e.getMessage( ), e );
            throw new Exception( strError );
        }

        return response;
    }

    /*** HTTP METHODS ***/

    /**
     * call get method
     * 
     * @param strUrl
     * @param mapHeaders
     * @param typeSubscription
     * @return the response message
     * @throws IOException
     */
    private String callDoGet( String strUrl, Map<String, String> mapHeaders ) throws IOException
    {
        HttpAccess httpAccess = new HttpAccess( );

        String strResponse = StringUtils.EMPTY;

        try
        {
            strResponse = httpAccess.doGet( strUrl, null, null, mapHeaders );
        }
        catch( HttpAccessException e )
        {
            AppLogService.error( "Returned Dolist error : " + strResponse );
        }

        return strResponse;
    }

    /**
     * call post method
     * 
     * @param strUrl
     * @param params
     * @param mapHeaders
     * @return true or false
     * @throws IOException
     * @throws HttpAccessException
     */
    private String callDoPost( String strUrl, String jsonParams, Map<String, String> mapHeaders ) throws IOException, HttpAccessException
    {
        DolistHttpAccess dlhttpaccess = new DolistHttpAccess( );

        return dlhttpaccess.doPost( strUrl, jsonParams, mapHeaders );
    }

    /**
     * call post method
     * 
     * @param strUrl
     * @param params
     * @param mapHeaders
     * @return true or false
     * @throws IOException
     * @throws HttpAccessException
     */
    private String callDoPut( String strUrl, String jsonParams, Map<String, String> mapHeaders ) throws IOException, HttpAccessException
    {
        DolistHttpAccess dlhttpaccess = new DolistHttpAccess( );

        return dlhttpaccess.doPut( strUrl, jsonParams, mapHeaders );
    }

    private Map<String, String> constructHeader( String strAccountId )
    {
        Map<String, String> mapHeader = new HashMap<>( );
        
        String strApiKey = CONSTANTE_HEADER_X_API_KEY;
        if ( StringUtils.isNotEmpty( strAccountId ) && !strAccountId.equals( PROPERTY_ACCOUNT_ID )  )
        {
            strApiKey = AppPropertiesService.getProperty( CONSTANTE_HEADER_X_API_KEY_PREFIX + strAccountId  ) ;
        }
        
        mapHeader.put( MARK_HEADER_X_API_KEY, strApiKey );
        mapHeader.put( MARK_HEADER_CONTENT_TYPE, CONSTANTE_HEADER_CONTENT_TYPE );
        mapHeader.put( MARK_HEADER_ACCEPT, CONSTANTE_HEADER_ACCEPT );
        mapHeader.put( MARK_HEADER_ACCEPT_LANGUAGE, CONSTANTE_HEADER_ACCEPT_LANGUAGE );

        return mapHeader;
    }
    
}
