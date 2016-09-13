package com.liufan.xhttp;

abstract class Platform {
	private static Platform _INSTANCE = findRightPlatform();

	static Platform get() {
		return _INSTANCE;
	}

	abstract Executor getExecutor();

	static Platform findRightPlatform() {
		try {
			Class.forName("android.os.Build");
			return new Android();
		} catch (Exception e) {
			return new Java8();
		}

	}

	interface Executor {
		public void execut(Runnable r);
	}

	static class Java8 extends Platform {
		@Override
		Executor getExecutor() {
			return new Java8Executor();
		}

		static class Java8Executor implements Executor {

			@Override
			public void execut(Runnable r) {
				r.run();
			}
		}
	}

	static class Android extends Platform {

		@Override
		Executor getExecutor() {
			return new AndroidExecutor();
		}

		static class AndroidExecutor implements Executor {
			android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());

			@Override
			public void execut(Runnable r) {
				handler.post(r);
			}
		}

	}
}
