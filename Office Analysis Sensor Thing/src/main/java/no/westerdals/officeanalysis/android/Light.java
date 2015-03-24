package no.westerdals.officeanalysis.android;

public class Light
{
    private String value;
    private String integrationId;

    public Light(final String value, final String integrationId)
    {
        this.value = value;
        this.integrationId = integrationId;
    }

    public String getValue()
    {
        return value;
    }

    public String getIntegrationId()
    {
        return integrationId;
    }

    @Override
    public String toString()
    {
        return "Light{" +
                "value='" + value + '\'' +
                ", integrationId='" + integrationId + '\'' +
                '}';
    }
}
