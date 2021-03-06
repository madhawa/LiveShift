package net.liveshift.signaling.messaging;

import java.math.BigInteger;
import java.util.Map;

import net.liveshift.core.PeerId;
import net.liveshift.util.Utils;




public class PshApplyRequestMessage extends AbstractMessage {

	final float amount;
	final PshCheck<PeerId> pshCheck;

	public PshApplyRequestMessage(float amount, PshCheck<PeerId> check, final PeerId myPeerId, final PeerId receiver) {
		super(myPeerId, receiver);

		this.amount = amount;
		this.pshCheck = check;
	}
	
	public PshApplyRequestMessage(final byte messageId, PeerId sender, final byte[] byteArray, final Map<BigInteger, PeerId> peerCatalog, int offset) {
		super(messageId, sender);
		
		byte[] intermediateDhtIt  = new byte[20];
		System.arraycopy(byteArray, offset+24, intermediateDhtIt, 0, 20);
		PeerId intermediate = peerCatalog.get(new BigInteger(intermediateDhtIt));

		byte[] targetDhtIt  = new byte[20];
		System.arraycopy(byteArray, offset+44, targetDhtIt, 0, 20);
		PeerId target = peerCatalog.get(new BigInteger(targetDhtIt));

		float amount = Utils.byteArrayToFloat(byteArray, offset+64);
		
		this.pshCheck = new PshCheck<PeerId>(intermediate, target, amount);
		
		byte[] signature = new byte[128];
		System.arraycopy(byteArray, offset+68, signature, 0, signature.length);
		
		this.pshCheck.setSignature(signature);
		
		this.amount = Utils.byteArrayToFloat(byteArray, offset+196);

	}

	@Override
	public byte[] toByteArray() {
		byte[] out = new byte[200];
		
		int offset = super.toByteArray(out, 0);
		out[offset++] = 'N';
		
		offset = this.pshCheck.getIntermediate().getDhtId().getIdAsByteArray(out, offset);
		offset = this.pshCheck.getTarget().getDhtId().getIdAsByteArray(out, offset);
		offset = Utils.floatToByteArray(this.pshCheck.getAmount(), out, offset);
		
		byte[] signature = this.pshCheck.getSignature();
		System.arraycopy(signature, 0, out, offset, signature.length);
		
		offset = Utils.floatToByteArray(this.amount, out, offset+=signature.length);

		return out;
	}

	
	public double getAmount() {
		return this.amount;
	}
	public PshCheck<PeerId> getCheck() {
		return this.pshCheck;
	}

	@Override
	public String toString() {
		String out = super.toString() + " amount: "+this.amount+ " check:"+this.pshCheck.toString();
		
		return out;
	}
}
