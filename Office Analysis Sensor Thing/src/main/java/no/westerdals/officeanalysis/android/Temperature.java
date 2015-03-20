package no.westerdals.officeanalysis.android;

public class Temperature
{
    private String value;
    private String timestamp;

    public Temperature(final String value, final String timestamp)
    {
        this.value = value;
        this.timestamp = timestamp;
    }

    @Override
    public String toString()
    {
        return "Temperature{" +
                "value='" + value + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
