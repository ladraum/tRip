package trip.spi;

public class ClosureProvider implements ProviderFactory<Closure> {

	@Override
	public Closure provide() {
		return new PeriodClosure();
	}

	class PeriodClosure implements Closure {

		@Override
		public Character getSentenceClosureChar() {
			return Closure.PERIOD;
		}

	}
}
