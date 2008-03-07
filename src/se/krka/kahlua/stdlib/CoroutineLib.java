package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.LuaException;
import se.krka.kahlua.vm.LuaState;
import se.krka.kahlua.vm.LuaTable;
import se.krka.kahlua.vm.LuaThread;

public class CoroutineLib implements JavaFunction {

	private static final int CREATE = 0;
	private static final int RESUME = 1;
	private static final int YIELD = 2;
	private static final int STATUS = 3;
	private static final int RUNNING = 4;

	private static final int NUM_FUNCTIONS = 5;
	
	
	private static final String[] names;
	static {
		names = new String[NUM_FUNCTIONS];
		names[CREATE] = "create";
		names[RESUME] = "resume";
		names[YIELD] = "yield";
		names[STATUS] = "status";
		names[RUNNING] = "running";
	}

	private int index;
	private static CoroutineLib[] functions;	
	static {
		functions = new CoroutineLib[NUM_FUNCTIONS];
		for (int i = 0; i < NUM_FUNCTIONS; i++) {
			functions[i] = new CoroutineLib(i);
		}
	}
	
	public CoroutineLib(int index) {
		this.index = index;
	}

	public static void register(LuaState state) {
		LuaTable coroutine = new LuaTable();
		state.environment.rawset("coroutine", coroutine);
		for (int i = 0; i < NUM_FUNCTIONS; i++) {
			coroutine.rawset(names[i], functions[i]);
		}
		
		coroutine.rawset("__index", coroutine);
		state.setUserdataMetatable(LuaThread.class, coroutine);
	}
	
	public int call(LuaCallFrame callFrame, int nArguments) {
		switch (index) {
		case CREATE: return create(callFrame, nArguments);
		case YIELD: return yield(callFrame, nArguments);
		case RESUME: return resume(callFrame, nArguments);
		case STATUS: return status(callFrame, nArguments);
		case RUNNING: return running(callFrame, nArguments);
		default:
			// Should never happen
			// throw new Error("Illegal function object");
			return 0;
		}
	}

	private int running(LuaCallFrame callFrame, int nArguments) {
		throw new RuntimeException("NYI: coroutine.running");
		/*
		LuaThread t = state.currentThread;
		
		// return nil if the thread is the root thread 
		if (t.parent == null) {
			t = null;
		}
		
		state.currentThread.objectStack[base] = t;
		return 1;
		*/
	}

	private int status(LuaCallFrame callFrame, int nArguments) {
		throw new RuntimeException("NYI: coroutine.status");
		/*
		LuaThread t = getCoroutine(state, base, arguments);
		
		String status = null;
		if (t == state.currentThread) {
			status = "running";
		} else if (t.status == LuaThread.THREADSTATUS_SUSPENDED) {
			status = "suspended";
		} else if (t.status == LuaThread.THREADSTATUS_NORMAL) {
			status = "normal";
		} else if (t.status == LuaThread.THREADSTATUS_DEAD) {
			status = "dead";
		}
		state.currentThread.objectStack[base] = status;
		*/
		//return 1;
	}

	private int resume(LuaCallFrame callFrame, int nArguments) {
		if (true) {
			throw new RuntimeException("NYI: coroutine.resume");
		}
		
		LuaThread t = getCoroutine(callFrame, nArguments);
		
		if (t.status != LuaThread.THREADSTATUS_SUSPENDED) {
			throw new LuaException("Can not resume a running thread");
		}
		
		LuaThread parent = callFrame.thread;
		t.parent = parent;
		callFrame.thread.state.currentThread = t;
		
		// copy arguments
		return LuaState.RETURN_RESUME;
	}

	private int yield(LuaCallFrame callFrame, int nArguments) {
		if (true) {
			throw new RuntimeException("NYI: coroutine.yield");
		}
		// TODO: check if valid to yield here
		// TODO: copy args, et.c.
		return LuaState.RETURN_YIELD;
	}

	private int create(LuaCallFrame callFrame, int nArguments) {
		if (true) {
			throw new RuntimeException("NYI: coroutine.create");
		}
		
		LuaClosure c = getFunction(callFrame, nArguments);

		LuaThread newThread = new LuaThread(callFrame.thread.state);
		LuaCallFrame newCallFrame = newThread.pushNewCallFrame(1, 0, 0, true, true);
		newCallFrame.init(c);
		
		callFrame.push(newThread);
		return 1;
	}

	private LuaClosure getFunction(LuaCallFrame callFrame, int nArguments) {
		BaseLib.luaAssert(nArguments >= 1, "not enough arguments");
		Object o = callFrame.get(0);
		BaseLib.luaAssert(o instanceof LuaClosure, "argument 1 must be a lua function");
		LuaClosure c = (LuaClosure) o;
		return c;
	}

	private LuaThread getCoroutine(LuaCallFrame callFrame, int nArguments) {
		BaseLib.luaAssert(nArguments >= 1, "not enough arguments");
		Object o = callFrame.get(0);
		BaseLib.luaAssert(o instanceof LuaThread, "argument 1 must be a coroutine");
		LuaThread t = (LuaThread) o;
		return t;
	}
}