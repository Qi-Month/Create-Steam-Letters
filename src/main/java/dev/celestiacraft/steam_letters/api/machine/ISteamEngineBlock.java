package dev.celestiacraft.steam_letters.api.machine;

/**
 * 由四类蒸汽设备的方块实现, 让 BlockEntity 能通过
 * {@code ((ISteamEngineBlock) getBlockState().getBlock()).getEngineSpec()}
 * 取回注册时写入的机制参数.
 * <p>
 * 参数放在方块上而不是 BlockEntity 上: 同一个 BlockEntityType 会服务多个不同参数的方块,
 * 放在方块上才不会互相覆盖, 也不需要额外同步.
 */
public interface ISteamEngineBlock {
	SteamEngineSpec getEngineSpec();
}