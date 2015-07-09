package trip.spi;

import lombok.Getter;

@Getter
@GeneratedFromStatelessService
@Singleton( name="FakeStatelessService" )
public class MyFakeStatelessService extends Readable {

	@Provided
	Printable printable;
}