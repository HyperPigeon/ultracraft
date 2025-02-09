package absolutelyaya.ultracraft.block;

import absolutelyaya.ultracraft.registry.ItemRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class PedestalBlock extends BlockWithEntity implements IPunchableBlock, BlockEntityProvider
{
	public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);
	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final Property<Boolean> FANCY = BooleanProperty.of("fancy");
	
	public PedestalBlock(Settings settings)
	{
		super(settings);
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx)
	{
		return super.getDefaultState().with(TYPE, Type.NONE).with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(FANCY, false);
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(TYPE, FACING, FANCY);
	}
	
	@Override
	public boolean onPunch(PlayerEntity puncher, BlockPos pos, boolean mainHand)
	{
		BlockEntity blockEntity = puncher.world.getBlockEntity(pos);
		if(blockEntity instanceof PedestalBlockEntity pedestal)
		{
			boolean result = pedestal.onPunch(puncher, mainHand);
			if(!puncher.world.isClient)
				puncher.world.emitGameEvent(puncher, GameEvent.BLOCK_CHANGE, pos);
			puncher.world.updateNeighbors(pos, this);
			puncher.world.updateNeighbors(pos.down(), this);
			return result;
		}
		return false;
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new PedestalBlockEntity(pos, state);
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
	{
		if(state.getBlock() != newState.getBlock())
		{
			BlockEntity entity = world.getBlockEntity(pos);
			if(entity instanceof PedestalBlockEntity pedestal)
			{
				ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), pedestal.getStack());
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
		world.updateNeighbors(pos.down(), this);
	}
	
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state)
	{
		return true;
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if(blockEntity instanceof PedestalBlockEntity pedestal)
		{
			ItemStack stack = pedestal.getStack();
			boolean b = state.get(TYPE).equals(Type.BLUE) && stack.getItem().equals(ItemRegistry.BLUE_SKULL) ||
								state.get(TYPE).equals(Type.RED) && stack.getItem().equals(ItemRegistry.RED_SKULL);
			return b ? 15 : 0;
		}
		return 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction)
	{
		return getWeakRedstonePower(state, world, pos, direction);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		ItemStack stack = player.getStackInHand(hand);
		if(stack.isOf(Items.BLUE_DYE) && !state.get(TYPE).equals(Type.BLUE))
		{
			useDye(world, pos, state, player, stack, Type.BLUE);
			return ActionResult.CONSUME;
		}
		else if(stack.isOf(Items.RED_DYE) && !state.get(TYPE).equals(Type.RED))
		{
			useDye(world, pos, state, player, stack, Type.RED);
			return ActionResult.CONSUME;
		}
		else if(stack.isOf(Items.WATER_BUCKET) && !state.get(TYPE).equals(Type.NONE) || state.get(FANCY))
		{
			world.setBlockState(pos, state.with(TYPE, Type.NONE).with(FANCY, false));
			if(!player.isCreative())
				player.setStackInHand(hand, new ItemStack(Items.BUCKET));
			world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1f, 1f);
			for (int i = 0; i < 16; i++)
			{
				Vec3d ppos = new Vec3d(world.random.nextDouble(), world.random.nextDouble(), world.random.nextDouble());
				world.addParticle(ParticleTypes.SPLASH, pos.getX() + ppos.x, pos.getY() + ppos.y, pos.getZ() + ppos.z,
						0f, 0f, 0f);
			}
			return ActionResult.CONSUME;
		}
		else if (!state.get(FANCY) && stack.isOf(Items.GLOWSTONE_DUST))
		{
			if(!player.isCreative())
				stack.decrement(1);
			world.setBlockState(pos, state.with(FANCY, true));
			Random rand = world.getRandom();
			world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.PLAYERS, 1f, 1.25f + rand.nextFloat() * 0.25f);
			for (int i = 0; i < 16; i++)
			{
				Vec3d p = new Vec3d(pos.getX() + rand.nextDouble(), pos.getY() + rand.nextDouble(), pos.getZ() + rand.nextDouble());
				world.addParticle(ParticleTypes.WAX_ON, p.x, p.y, p.z, 0f, 0f, 0f);
			}
			return ActionResult.CONSUME;
		}
		else return super.onUse(state, world, pos, player, hand, hit);
	}
	
	void useDye(World world, BlockPos pos, BlockState state, PlayerEntity player, ItemStack dye, Type newType)
	{
		world.setBlockState(pos, state.with(TYPE, newType));
		if(!player.isCreative())
			dye.decrement(1);
		world.playSound(null, pos, SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER, SoundCategory.PLAYERS, 1f, 1f);
		world.addBlockBreakParticles(pos, state.with(TYPE, newType));
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return VoxelShapes.cuboid(1f / 16f, 0, 1f / 16f, 1 - 1f / 16f, 1f, 1 - 1f / 16f);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return getCollisionShape(state, world, pos, context);
	}
	
	public enum Type implements StringIdentifiable
	{
		NONE("none"),
		BLUE("blue"),
		RED("red");
		
		final String name;
		Type(String name)
		{
			this.name = name;
		}
		
		@Override
		public String asString()
		{
			return name;
		}
	}
}
