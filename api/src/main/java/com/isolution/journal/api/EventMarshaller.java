package com.isolution.journal.api;

public interface EventMarshaller<$Class, $Source, $Destination> {

    void marshall($Class instance, $Destination destination);

    $Class unmarshall($Source source);
}
