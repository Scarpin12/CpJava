package br.com.fiap.campusgigs.contratacao;

import br.com.fiap.campusgigs.servico.Servico;
import br.com.fiap.campusgigs.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "contratacao")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contratacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id")
    private Servico servico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contratante_id")
    private Usuario contratante;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SituacaoContratacao situacao = SituacaoContratacao.SOLICITADA;

    @Builder.Default
    private LocalDateTime dataContratacao = LocalDateTime.now();
}
