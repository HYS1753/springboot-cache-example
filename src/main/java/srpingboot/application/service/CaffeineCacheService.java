package srpingboot.application.service;

import srpingboot.application.dto.TestInput;
import srpingboot.application.dto.TestResult;

public interface CaffeineCacheService {

    TestResult enableCache(TestInput request);

    TestResult evictCache(TestInput request);
}
