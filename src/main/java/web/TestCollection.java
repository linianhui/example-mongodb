package web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "test_collection")
@CompoundIndexes({
    @CompoundIndex(
        name = "code_1",
        unique = true,
        background = true,
        def = "{'code':1}"
    )
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestCollection {
  private String id;
  private String code;
  private String name;
}
