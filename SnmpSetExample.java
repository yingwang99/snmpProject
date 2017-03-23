import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;


public class SnmpSetExample
{
  private static String  ipAddress  = "[aaaa::206:98ff:fe00:232]";

  private static String  port    = "1610";
 
  private static int snmpVersion  = SnmpConstants.version1;

  private static String  community  = "public";
  
  private TransportMapping transport;

  public void start() throws IOException{
	// Create TransportMapping and Listen
    transport = new DefaultUdpTransportMapping();
    transport.listen();
  }

  public CommunityTarget getTarget(){
	// Create Target Address object
    CommunityTarget comtarget = new CommunityTarget();
    comtarget.setCommunity(new OctetString(community));
    comtarget.setVersion(snmpVersion);
    comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
    comtarget.setRetries(2);
    comtarget.setTimeout(1000);
	
    return comtarget;
	
  } 

  public void set(String oid, String value, int integerValue) throws IOException{
	
    
    // Create the PDU object
    PDU pdu = new PDU();
   
    // Setting the Oid and Value for sysContact variable
    OID oids = new OID(oid);
    VariableBinding varBind;
    if(!value.equals("")){
    	Variable var = new OctetString(value);
        varBind = new VariableBinding(oids,var);
    }else{

        varBind = new VariableBinding(oids,new Integer32(integerValue));
    }
    pdu.add(varBind);
   
    pdu.setType(PDU.SET);
    pdu.setRequestID(new Integer32(1));

    // Create Snmp object for sending data to Agent
    Snmp snmp = new Snmp(transport);

    System.out.println("\nRequest:\n[ Note: Set Request is sent for sysContact oid in RFC 1213 MIB.");
 
    System.out.println("Once this operation is completed, Querying for sysContact will get the value");
   
    System.out.println("Request:\nSending Snmp Set Request to Agent...");

    ResponseEvent response = snmp.set(pdu, getTarget());

    // Process Agent Response
    if (response != null)
    {
      System.out.println("\nResponse:\nGot Snmp Set Response from Agent");
    }
      
    snmp.close();
	}

  public static void main(String[] args) throws Exception
  {
 
   SnmpSetExample client = new SnmpSetExample();
   client.start();
   System.out.println("Senarios 1 ------ set system description to iot project");
   client.set("1.3.6.1.2.1.1.1.0","sensor project",0);
 System.out.println("Senarios 2 ------ set system temperature to 40");
   client.set("1.3.6.1.2.1.1.10.0","",50);

System.out.println("Senarios 3 ------ set system sysVoltage to 4");
   client.set("1.3.6.1.2.1.1.12.0","",4);


  }
}
