package com.mkalugin.corchy.internal.ui;

public enum Alignment {

	LEFT {
		@Override
		public float position(float min, float max, float length) {
			return min;
		}
	},
	RIGHT {
		@Override
		public float position(float min, float max, float length) {
			return max - length;
		}
	},
	CENTER {
		@Override
		public float position(float min, float max, float length) {
			return (min + max) / 2 - length / 2;
		}
	};
	
	public abstract float position(float min, float max, float length);

}
