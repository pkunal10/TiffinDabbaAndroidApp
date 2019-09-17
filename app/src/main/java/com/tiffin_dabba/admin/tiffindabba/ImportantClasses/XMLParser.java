package com.tiffin_dabba.admin.tiffindabba.ImportantClasses;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by ADMIN on 15-09-2017.
 */

public class XMLParser {


    public static SoapObject getServiceResultSoapObj(String URL, String SOAP_ACTION, SoapObject request)
            throws XmlPullParserException, IOException {
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;

        // Set output SOAP object
        envelope.setOutputSoapObject(request);

        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        androidHttpTransport.debug = true;

        SoapObject response;

        // StringBuffer result = null;

        System.setProperty("http.keepAlive", "false");

        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the response
            response = (SoapObject) envelope.getResponse();
            StringBuffer result;
            result = new StringBuffer(response.toString());
            Log.d("Result", result.toString());

        } catch (SoapFault e) {
            // Log.e(TAG, "SoapFaultException");
            throw e;
        } catch (XmlPullParserException e) {
            //  Log.e(TAG, "XmlPullParserException");
            throw e;
        } catch (IOException e) {
            //  Log.e(TAG, "IOException");
            throw e;
        }
        return response;
    }

    public String getServiceResultString(String URL,String SOAP_ACTION,SoapObject request)
    {
        String res = "";
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(URL);
        httpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        httpTransport.debug = true;

        try {
            httpTransport.call(SOAP_ACTION, envelope);
            Object response = envelope.getResponse();

            res = response.toString();

        } catch (Exception exception) {
            //tv.setText(exception.toString());
            res = exception.toString();
        }

        return res;
    }

}
