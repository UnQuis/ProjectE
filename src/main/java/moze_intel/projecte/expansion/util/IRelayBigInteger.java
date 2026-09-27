package moze_intel.projecte.expansion.util;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;

import java.math.BigInteger;

public interface IRelayBigInteger extends IEmcStorage {
	//Note: 1.20.4's IEmcStorage has no getBonusToAdd, this is the bridge for the tooltip code
	default double getBonusToAdd() {
		return Util.safeLongValue(getBonusToAddBigInteger());
	}

	BigInteger getBonusToAddBigInteger();
}
