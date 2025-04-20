package example.cashcard;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class CashCardJsonTests {
    @Autowired
    private JacksonTester<CashCard> json;

    @Autowired
    private JacksonTester<CashCard[]> jsonList;

    private CashCard[] cashCards;

    @BeforeEach
    void setUp() {
        cashCards = Arrays.array(
                new CashCard(99L, 123.45, "sarah1"),
                new CashCard(100L, 1.00, "sarah1"),
                new CashCard(101L, 150.00, "sarah1"));
    }

    @Test
    void cashCardSerializationTest() throws IOException {
        var cashCard = new CashCard(99L, 123.45, "sarah1");
        var jsonCashCard = json.write(cashCard);

        assertThat(jsonCashCard).isStrictlyEqualToJson("single.json");

        assertThat(jsonCashCard).hasJsonPathNumberValue("@.id");
        assertThat(jsonCashCard)
                .extractingJsonPathNumberValue("@.id")
                .isEqualTo(99);

        assertThat(jsonCashCard).hasJsonPathNumberValue("@.amount");
        assertThat(jsonCashCard)
                .extractingJsonPathNumberValue("@.amount")
                .isEqualTo(123.45);
    }

    @Test
    void cashCardDeserializationTest() throws IOException {
        String expected = """
                {
                    "id": 99,
                    "amount": 123.45,
                    "owner": "sarah1"
                }
                """;

        assertThat(json.parse(expected)).isEqualTo(new CashCard(99L, 123.45, "sarah1"));
        assertThat(json.parseObject(expected).id()).isEqualTo(99L);
        assertThat(json.parseObject(expected).amount()).isEqualTo(123.45);
    }

    @Test
    void cashCardListSerializationTest() throws IOException {
        assertThat(jsonList.write(cashCards)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void cashCardListDeserializationTest() throws IOException {
        String expected = """
                [
                    {
                        "id": 99,
                        "amount": 123.45,
                        "owner": "sarah1"
                    },
                    {
                        "id": 100,
                        "amount": 1.00,
                        "owner": "sarah1"
                    },
                    {
                        "id": 101,
                        "amount": 150.00,
                        "owner": "sarah1"
                    }
                ]
                """;

        assertThat(jsonList.parse(expected)).isEqualTo(cashCards);
        assertThat(jsonList.parseObject(expected)[0].id()).isEqualTo(99L);
        assertThat(jsonList.parseObject(expected)[0].amount()).isEqualTo(123.45);
    }
}
