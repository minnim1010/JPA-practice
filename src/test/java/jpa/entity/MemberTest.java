package jpa.entity;

import jpa.JpaExecutionInterface;
import jpa.TestConfig;
import jpa.dto.MemberResponse;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

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

    @DisplayName("DTO를 기반으로 데이터를 하나 조회한다.")
    @Test
    void test4() {
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

        //when
        Query query = em.createQuery("SELECT NEW jpa.dto.MemberResponse(m.id, m.name, m.address) FROM Member AS m");
        MemberResponse result = (MemberResponse) query.getSingleResult();

        //then
        assertThat(result)
            .extracting("id", "name", "address.city", "address.street", "address.zipcode")
            .contains(1L, "member", "서울", "ㅁㅁ로", "13243");
    }

    @DisplayName("DTO를 기반으로 데이터를 여러 개 조회한다.")
    @Test
    void test5() {
        //given
        Member member1 = Member.builder()
            .name("member1")
            .address(Address.builder()
                .city("서울")
                .street("ㅁㅁ로")
                .zipcode("13243")
                .build())
            .build();

        Member member2 = Member.builder()
            .name("member2")
            .address(Address.builder()
                .city("경기")
                .street("ㅁㅁ로")
                .zipcode("23443")
                .build())
            .build();

        Member member3 = Member.builder()
            .name("member3")
            .address(Address.builder()
                .city("대전")
                .street("ㅁㅁ로")
                .zipcode("4533")
                .build())
            .build();

        JpaExecutionInterface.execute(em.getTransaction(), () -> {
            em.persist(member1);
            em.persist(member2);
            em.persist(member3);
        });

        //when
        TypedQuery<MemberResponse> query = em.createQuery("SELECT NEW jpa.dto.MemberResponse(m.id, m.name, m.address) FROM Member AS m", MemberResponse.class);
        List<MemberResponse> resultList = query.getResultList();

        //then
        assertThat(resultList).hasSize(3)
            .extracting("id", "name", "address.city", "address.street", "address.zipcode")
            .containsExactlyInAnyOrder(
                Tuple.tuple(1L, "member1", "서울", "ㅁㅁ로", "13243"),
                Tuple.tuple(2L, "member2", "경기", "ㅁㅁ로", "23443"),
                Tuple.tuple(3L, "member3", "대전", "ㅁㅁ로", "4533")
            );
    }
}