package srpingboot.application.service;

import srpingboot.application.dto.TestInput;
import srpingboot.application.dto.TestResult;

public interface RedisCacheService {

    TestResult enableCache(TestInput request);

    TestResult evictCache(TestInput request);
}
