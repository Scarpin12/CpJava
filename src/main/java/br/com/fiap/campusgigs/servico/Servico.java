package br.com.fiap.campusgigs.servico;

import br.com.fiap.campusgigs.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "servico")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prestador_id")
    private Usuario prestador;

    private String titulo;

    private String descricao;

    private String categoria;

    private BigDecimal preco;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SituacaoServico situacao = SituacaoServico.ATIVO;
}
