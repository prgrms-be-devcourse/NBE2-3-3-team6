package com.redbox.domain.user.entity;

import com.redbox.domain.user.exception.EmptyPasswordException;
import com.redbox.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

class UserTest {

    @DisplayName("사용자의 비밀번호가 바뀌었는지 확인한다.")
    @Test
    void changePassword() throws Exception {
        //given
        User user = User.builder()
                .password("123456")
                .build();
        //when
        String newPassword = "999999";
        user.changePassword(newPassword);
        
        //then
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }
    
    @DisplayName("바꾸려는 비밀번호가 null 값이면 예외가 발생한다.")
    @Test
    void changePasswordWithNullThrowsException() throws Exception {
        //given
        User user = User.builder()
                .password("123456")
                .build();
        
        //when & then
        assertThatThrownBy(() -> user.changePassword(null))
                .isInstanceOf(EmptyPasswordException.class)
                .hasMessage("비밀번호는 비어있을 수 없습니다.")
                .asInstanceOf(type(BusinessException.class))
                .extracting(ex -> ex.getErrorCodes().getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("바꾸려는 비밀번호가 빈 값이면 예외가 발생한다.")
    @Test
    void changePasswordWithEmptyThrowsException() throws Exception {
        //given
        User user = User.builder()
                .password("123456")
                .build();

        //when & then
        assertThatThrownBy(() -> user.changePassword(""))
                .isInstanceOf(EmptyPasswordException.class)
                .hasMessage("비밀번호는 비어있을 수 없습니다.")
                .asInstanceOf(type(BusinessException.class))
                .extracting(ex -> ex.getErrorCodes().getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}