package dev.rdh.png.chunk.data;

public interface Filter {
	byte[] filter(byte[] prev, byte[] data);

	byte[] unfilter(byte[] prev, byte[] data);

	public enum FilterType implements Filter {
		NONE {
			@Override
			public byte[] filter(byte[] prev, byte[] data) {
				return data;
			}

			@Override
			public byte[] unfilter(byte[] prev, byte[] data) {
				return data;
			}
		},

		SUB {
			@Override
			public byte[] filter(byte[] prev, byte[] data) {
				byte[] filtered = new byte[data.length];
				for(int i = 0; i < data.length; i++) {
					byte left = i < 3 ? 0 : data[i - 3];
					filtered[i] = (byte) (data[i] - left);
				}
				return filtered;
			}

			@Override
			public byte[] unfilter(byte[] prev, byte[] data) {
				byte[] unfiltered = new byte[data.length];
				for(int i = 0; i < data.length; i++) {
					byte left = i < 3 ? 0 : unfiltered[i - 3];
					unfiltered[i] = (byte) (data[i] + left);
				}
				return unfiltered;
			}
		},

		UP {
			@Override
			public byte[] filter(byte[] prev, byte[] data) {
				byte[] filtered = new byte[data.length];
				for(int i = 0; i < data.length; i++) {
					byte up = prev == null ? 0 : prev[i];
					filtered[i] = (byte) (data[i] - up);
				}
				return filtered;
			}

			@Override
			public byte[] unfilter(byte[] prev, byte[] data) {
				byte[] unfiltered = new byte[data.length];
				for(int i = 0; i < data.length; i++) {
					byte up = prev == null ? 0 : unfiltered[i];
					unfiltered[i] = (byte) (data[i] + up);
				}
				return unfiltered;
			}
		},

		AVERAGE {
			@Override
			public byte[] filter(byte[] prev, byte[] data) {
				byte[] filtered = new byte[data.length];
				for(int i = 0; i < data.length; i++) {
					byte left = i < 3 ? 0 : data[i - 3];
					byte up = prev == null ? 0 : prev[i];
					filtered[i] = (byte) (data[i] - (left + up) / 2);
				}
				return filtered;
			}

			@Override
			public byte[] unfilter(byte[] prev, byte[] data) {
				byte[] unfiltered = new byte[data.length];
				for(int i = 0; i < data.length; i++) {
					byte left = i < 3 ? 0 : unfiltered[i - 3];
					byte up = prev == null ? 0 : prev[i];
					unfiltered[i] = (byte) (data[i] + (left + up) / 2);
				}
				return unfiltered;
			}
		},

		PAETH {
			@Override
			public byte[] filter(byte[] prev, byte[] data) {
				byte[] filtered = new byte[data.length];
				for(int i = 0; i < data.length; i++) {
					byte left = i < 3 ? 0 : data[i - 3];
					byte up = prev == null ? 0 : prev[i];
					byte upLeft = i < 3 || prev == null ? 0 : prev[i - 3];
					filtered[i] = (byte) (data[i] - predictor(left, up, upLeft));
				}
				return filtered;
			}

			@Override
			public byte[] unfilter(byte[] prev, byte[] data) {
				byte[] unfiltered = new byte[data.length];
				for(int i = 0; i < data.length; i++) {
					byte left = i < 3 ? 0 : unfiltered[i - 3];
					byte up = prev == null ? 0 : prev[i];
					byte upLeft = i < 3 || prev == null ? 0 : prev[i - 3];
					unfiltered[i] = (byte) (data[i] + predictor(left, up, upLeft));
				}
				return unfiltered;
			}

			private byte predictor(byte left, byte up, byte upLeft) {
				int p = left + up - upLeft;
				int pLeft = Math.abs(p - left);
				int pUp = Math.abs(p - up);
				int pUpLeft = Math.abs(p - upLeft);
				if(pLeft <= pUp && pLeft <= pUpLeft) {
					return left;
				} else if(pUp <= pUpLeft) {
					return up;
				} else {
					return upLeft;
				}
			}
		};

		private final int value;

		FilterType() {
			this.value = ordinal();
		}

		public static FilterType fromValue(int value) {
			return values()[value];
		}
	}
}

