package blah.tests;

public interface Converter<T> {

	T convert( String string ) throws ConverterException;
}
