import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeUtils;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.Target;



public class SnmpWalk {
	private String address;
	private Snmp snmp;

	SnmpWalk(String address){
		// Set default value.
		//targetAddr = "127.0.0.1";
		this.address = address;
	}
	
	private void start() throws IOException {
		TransportMapping transport = new DefaultUdpTransportMapping();
		snmp = new Snmp(transport);
		// Do not forget this line!
		transport.listen();
	}
	
	public String getAsString(OID oid) throws IOException {
		ResponseEvent event = get(new OID[]{oid});
		return event.getResponse().get(0).getVariable().toString();
	}

	public ResponseEvent get(OID oids[]) throws IOException {
		PDU pdu = new PDU();
	 	for (OID oid : oids) {
	 	     pdu.add(new VariableBinding(oid));
	 	}
	 	pdu.setType(PDU.GET);
			
	 	ResponseEvent event = snmp.send(pdu, getTarget(), null);
		if(event != null) {
		     return event;
		}
		throw new RuntimeException("GET timed out");
	}

	private Target getTarget() {
		Address targetAddress = new UdpAddress(address);
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString("public"));
		target.setAddress(targetAddress);
		target.setRetries(2);
		target.setTimeout(1500);
		target.setVersion(SnmpConstants.version1);
		return target;
	}

// Delegate main function to Snmpwalk.
public static void main(String[] args) throws IOException{
	SnmpWalk client = new SnmpWalk("[aaaa::206:98ff:fe00:232]/1610");
	client.start();
	String sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.1.0"));
	System.out.println(sysDescr);

	sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.3.0"));
	System.out.println(sysDescr);
	
	sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.5.0"));
	System.out.println(sysDescr);
        sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.10.0"));
	System.out.println(sysDescr); 
	sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.11.0"));
	System.out.println(sysDescr);
	sysDescr = client.getAsString(new OID(".1.3.6.1.2.1.1.12.0"));
	System.out.println(sysDescr);
       

	}
}
