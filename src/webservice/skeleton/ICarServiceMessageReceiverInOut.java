/**
 * ICarServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.7  Built on : Nov 20, 2017 (11:41:20 GMT)
 */
package webservice.skeleton;


/**
 *  ICarServiceMessageReceiverInOut message receiver
 */
public class ICarServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver {
    public void invokeBusinessLogic(
        org.apache.axis2.context.MessageContext msgContext,
        org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault {
        try {
            // get the implementation class for the Web Service
            Object obj = getTheImplementationObject(msgContext);

            ICarServiceSkeletonInterface skel = (ICarServiceSkeletonInterface) obj;

            //Out Envelop
            org.apache.axiom.soap.SOAPEnvelope envelope = null;

            //Find the axisOperation that has been set by the Dispatch phase.
            org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext()
                                                                      .getAxisOperation();

            if (op == null) {
                throw new org.apache.axis2.AxisFault(
                    "Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
            }

            java.lang.String methodName;

            if ((op.getName() != null) &&
                    ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(
                            op.getName().getLocalPart())) != null)) {
                if ("traceStolenProducts".equals(methodName)) {
                    webservice.TraceStolenProductsResponse traceStolenProductsResponse25 =
                        null;
                    webservice.TraceStolenProducts wrappedParam = (webservice.TraceStolenProducts) fromOM(msgContext.getEnvelope()
                                                                                                                    .getBody()
                                                                                                                    .getFirstElement(),
                            webservice.TraceStolenProducts.class);

                    traceStolenProductsResponse25 = skel.traceStolenProducts(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            traceStolenProductsResponse25, false,
                            new javax.xml.namespace.QName("http://webservice",
                                "traceStolenProductsResponse"));
                } else
                 if ("traceStolenParts".equals(methodName)) {
                    webservice.TraceStolenPartsResponse traceStolenPartsResponse27 =
                        null;
                    webservice.TraceStolenParts wrappedParam = (webservice.TraceStolenParts) fromOM(msgContext.getEnvelope()
                                                                                                              .getBody()
                                                                                                              .getFirstElement(),
                            webservice.TraceStolenParts.class);

                    traceStolenPartsResponse27 = skel.traceStolenParts(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            traceStolenPartsResponse27, false,
                            new javax.xml.namespace.QName("http://webservice",
                                "traceStolenPartsResponse"));
                } else
                 if ("traceStolenCar".equals(methodName)) {
                    webservice.TraceStolenCarResponse traceStolenCarResponse29 = null;
                    webservice.TraceStolenCar wrappedParam = (webservice.TraceStolenCar) fromOM(msgContext.getEnvelope()
                                                                                                          .getBody()
                                                                                                          .getFirstElement(),
                            webservice.TraceStolenCar.class);

                    traceStolenCarResponse29 = skel.traceStolenCar(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            traceStolenCarResponse29, false,
                            new javax.xml.namespace.QName("http://webservice",
                                "traceStolenCarResponse"));
                } else
                 if ("traceStolenPallets".equals(methodName)) {
                    webservice.TraceStolenPalletsResponse traceStolenPalletsResponse31 =
                        null;
                    webservice.TraceStolenPallets wrappedParam = (webservice.TraceStolenPallets) fromOM(msgContext.getEnvelope()
                                                                                                                  .getBody()
                                                                                                                  .getFirstElement(),
                            webservice.TraceStolenPallets.class);

                    traceStolenPalletsResponse31 = skel.traceStolenPallets(wrappedParam);

                    envelope = toEnvelope(getSOAPFactory(msgContext),
                            traceStolenPalletsResponse31, false,
                            new javax.xml.namespace.QName("http://webservice",
                                "traceStolenPalletsResponse"));
                } else {
                    throw new java.lang.RuntimeException("method not found");
                }

                newMsgContext.setEnvelope(envelope);
            }
        } catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    //
    private org.apache.axiom.om.OMElement toOM(
        webservice.TraceStolenProducts param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(webservice.TraceStolenProducts.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        webservice.TraceStolenProductsResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(webservice.TraceStolenProductsResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        webservice.TraceStolenParts param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(webservice.TraceStolenParts.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        webservice.TraceStolenPartsResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(webservice.TraceStolenPartsResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        webservice.TraceStolenCar param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(webservice.TraceStolenCar.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        webservice.TraceStolenCarResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(webservice.TraceStolenCarResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        webservice.TraceStolenPallets param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(webservice.TraceStolenPallets.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.om.OMElement toOM(
        webservice.TraceStolenPalletsResponse param, boolean optimizeContent)
        throws org.apache.axis2.AxisFault {
        try {
            return param.getOMElement(webservice.TraceStolenPalletsResponse.MY_QNAME,
                org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory,
        webservice.TraceStolenProductsResponse param, boolean optimizeContent,
        javax.xml.namespace.QName elementQName)
        throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(
                    webservice.TraceStolenProductsResponse.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private webservice.TraceStolenProductsResponse wraptraceStolenProducts() {
        webservice.TraceStolenProductsResponse wrappedElement = new webservice.TraceStolenProductsResponse();

        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory,
        webservice.TraceStolenPartsResponse param, boolean optimizeContent,
        javax.xml.namespace.QName elementQName)
        throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(
                    webservice.TraceStolenPartsResponse.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private webservice.TraceStolenPartsResponse wraptraceStolenParts() {
        webservice.TraceStolenPartsResponse wrappedElement = new webservice.TraceStolenPartsResponse();

        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory,
        webservice.TraceStolenCarResponse param, boolean optimizeContent,
        javax.xml.namespace.QName elementQName)
        throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(
                    webservice.TraceStolenCarResponse.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private webservice.TraceStolenCarResponse wraptraceStolenCar() {
        webservice.TraceStolenCarResponse wrappedElement = new webservice.TraceStolenCarResponse();

        return wrappedElement;
    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory,
        webservice.TraceStolenPalletsResponse param, boolean optimizeContent,
        javax.xml.namespace.QName elementQName)
        throws org.apache.axis2.AxisFault {
        try {
            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

            emptyEnvelope.getBody()
                         .addChild(param.getOMElement(
                    webservice.TraceStolenPalletsResponse.MY_QNAME, factory));

            return emptyEnvelope;
        } catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
    }

    private webservice.TraceStolenPalletsResponse wraptraceStolenPallets() {
        webservice.TraceStolenPalletsResponse wrappedElement = new webservice.TraceStolenPalletsResponse();

        return wrappedElement;
    }

    /**
     *  get the default envelope
     */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(
        org.apache.axiom.soap.SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    private java.lang.Object fromOM(org.apache.axiom.om.OMElement param,
        java.lang.Class type) throws org.apache.axis2.AxisFault {
        try {
            if (webservice.TraceStolenCar.class.equals(type)) {
                return webservice.TraceStolenCar.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (webservice.TraceStolenCarResponse.class.equals(type)) {
                return webservice.TraceStolenCarResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (webservice.TraceStolenPallets.class.equals(type)) {
                return webservice.TraceStolenPallets.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (webservice.TraceStolenPalletsResponse.class.equals(type)) {
                return webservice.TraceStolenPalletsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (webservice.TraceStolenParts.class.equals(type)) {
                return webservice.TraceStolenParts.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (webservice.TraceStolenPartsResponse.class.equals(type)) {
                return webservice.TraceStolenPartsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (webservice.TraceStolenProducts.class.equals(type)) {
                return webservice.TraceStolenProducts.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }

            if (webservice.TraceStolenProductsResponse.class.equals(type)) {
                return webservice.TraceStolenProductsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());
            }
        } catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

        return null;
    }

    private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
        org.apache.axis2.AxisFault f;
        Throwable cause = e.getCause();

        if (cause != null) {
            f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
        } else {
            f = new org.apache.axis2.AxisFault(e.getMessage());
        }

        return f;
    }
} //end of class
