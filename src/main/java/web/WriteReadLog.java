package web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "write_read_log")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriteReadLog {
  private String write;
  private String read;
}
