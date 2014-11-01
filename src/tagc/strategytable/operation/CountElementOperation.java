package tagc.strategytable.operation;

public class CountElementOperation implements PureOperation<Integer> {
	
	private Integer count = 0;

	@Override
	public void store(Integer data) {
		this.count = data;
	}

	@Override
	public Integer get() {
		return count;
	}
}
