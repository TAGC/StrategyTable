package operation;

public class FindTotalOperation implements PureOperation<Integer> {
	
	private Integer value = 0;

	@Override
	public void store(Integer value) {
		this.value = value;	
	}

	@Override
	public Integer get() {
		return value;
	}
}
