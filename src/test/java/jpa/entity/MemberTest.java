package jpa.entity;

import jpa.JpaExecutionInterface;
import jpa.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.Query;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTest extends TestConfig {

    @AfterEach
    void tearDown() {
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            Query memberDelete = em.createQuery("DELETE FROM Member m");
            memberDelete.executeUpdate();
        });
        em.clear();
    }

    @DisplayName("회원을 생성한다.")
    @Test
    void test1() {
        //given
        Member member = Member.builder()
            .name("member")
            .address(Address.builder()
                .city("서울")
                .street("ㅁㅁ로")
                .zipcode("13243")
                .build())
            .build();

        //when
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(member);
        });

        //then
        Member result = em.find(Member.class, 1L);
        assertThat(result)
            .extracting("name", "address.city", "address.street", "address.zipcode")
            .contains("member", "서울", "ㅁㅁ로", "13243");
    }

    @DisplayName("존재하는 회원을 수정한다.")
    @Test
    void test2() {
        //given
        Member member = Member.builder()
            .name("member")
            .address(Address.builder()
                .city("서울")
                .street("ㅁㅁ로")
                .zipcode("13243")
                .build())
            .build();

        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(member);
        });

        Member savedMember = em.find(Member.class, 1L);

        //when
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            savedMember.setName("updateMember");
        });

        //then
        Member result = em.find(Member.class, 1L);
        assertThat(result.getName()).isEqualTo("updateMember");
    }

    @DisplayName("존재하는 회원을 삭제한다.")
    @Test
    void test3() {
        //given
        Member member = Member.builder()
            .name("member")
            .address(Address.builder()
                .city("서울")
                .street("ㅁㅁ로")
                .zipcode("13243")
                .build())
            .build();

        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(member);
        });

        Member savedMember = em.find(Member.class, 1L);

        //when
        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.remove(savedMember);
        });

        //then
        assertThat(em.find(Member.class, 1L)).isEqualTo(null);
    }
}