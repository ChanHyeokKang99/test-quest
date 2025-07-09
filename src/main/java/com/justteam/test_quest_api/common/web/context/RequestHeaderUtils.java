package com.justteam.test_quest_api.common.web.context;


import com.justteam.test_quest_api.common.exception.NotFound;
import com.justteam.test_quest_api.jwt.authentication.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class RequestHeaderUtils {
    public static String getRequestHeaderParamAsString(String key) {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            log.info("Request Header Param: {}", requestAttributes);
        return requestAttributes.getRequest().getHeader(key);
    }

    public static UserPrincipal getAuthenticatedUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            return null;
        }
        return (UserPrincipal) authentication.getPrincipal();
    }

    public static String getUserId() {
        UserPrincipal userPrincipal = getAuthenticatedUserPrincipal();
        if (userPrincipal == null) {
            throw new NotFound("인증된 사용자 정보(User ID)가 SecurityContext에 없습니다.");
        }
        return userPrincipal.getUserId();
    }

//    public static String getUserId() {
//        return getRequestHeaderParamAsString("X-Auth-UserId");
//    }

    public static String getClientDevice() {
        String clientDevice = getRequestHeaderParamAsString("X-Client-Device");
        if (clientDevice == null) {
            return null;
        }

        return clientDevice;
    }

    public static String getClientAddress() {
        String clientAddress = getRequestHeaderParamAsString("X-Client-Address");
        if (clientAddress == null) {
            return null;
        }

        return clientAddress;
    }

    public static String getUserIdOrThrowException() {
        String userId = getUserId();
        if (userId == null) {
            throw new NotFound("헤더에 userId 정보가 없습니다.");
        }

        return userId;
    }

    public static String getClientDeviceOrThrowException() {
        String clientDevice = getClientDevice();
        if (clientDevice == null) {
            throw new NotFound("헤더에 사용자  디바이스 정보가 없습니다.");
        }

        return clientDevice;
    }

    public static String getClientAddressOrThrowException() {
        String clientAddress = getClientAddress();
        if (clientAddress == null) {
            throw new NotFound("헤더에 사용자 IP 주소 정보가 없습니다.");
        }

        return clientAddress;
    }
}
