
package com.andrepenteado.apscott.repositories;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.andrepenteado.apscott.models.Pagar;
import com.andrepenteado.apscott.models.Pago;

public interface PagarRepository extends JpaRepository<Pagar, Long> {

    @Query("SELECT p FROM Pagar p WHERE p.pagamentos IS EMPTY ORDER BY p.dataVencimento")
    List<Pagar> pesquisarPagamentosPendentes();

    @Query("SELECT SUM(p.valor) FROM Pagar p WHERE p.pagamentos IS EMPTY")
    BigDecimal somarTotal();

    @Query("SELECT SUM(p.valor) FROM Pagar p WHERE p.dataVencimento < CURRENT_DATE AND p.pagamentos IS EMPTY GROUP BY p.dataVencimento")
    BigDecimal somarTotalVencido();

    @Query("SELECT SUM(p.valor) FROM Pagar p WHERE p.dataVencimento = CURRENT_DATE AND p.pagamentos IS EMPTY")
    BigDecimal somarTotalVencendo();

    @Query("SELECT SUM(p.valor) FROM Pagar p WHERE p.dataVencimento > CURRENT_DATE AND p.pagamentos IS EMPTY")
    BigDecimal somarTotalVencer();

    @Query("SELECT SUM(p.valor), p.categoria.descricao FROM Pagar p WHERE p.pagamentos IS EMPTY GROUP BY p.categoria.descricao")
    List<Object> somarTotalPendenteAgrupadoPorCategoria();

    @Query("SELECT SUM(p.valor), p.dataVencimento FROM Pagar p WHERE p.pagamentos IS EMPTY GROUP BY p.dataVencimento ORDER BY p.dataVencimento")
    List<Object> somarTotalPendenteAgrupadoPorDia();

    @Query("SELECT p FROM Pago p WHERE lower(p.pagar.descricao) LIKE concat('%', lower(?1), '%') AND p.dataPagamento BETWEEN ?2 AND ?3 ORDER BY p.dataPagamento")
    List<Pago> pesquisarPagoPorDescricaoPorData(String descricao, Date dataInicio, Date dataFim);

    @Query("SELECT SUM(p.valorPago) FROM Pago p WHERE lower(p.pagar.descricao) LIKE concat('%', lower(?1), '%') AND p.dataPagamento BETWEEN ?2 AND ?3")
    BigDecimal somarPagoPorDescricaoPorData(String descricao, Date dataInicio, Date dataFim);

    @Query("SELECT SUM(p.valorPago), p.pagar.categoria.descricao FROM Pago p WHERE lower(p.pagar.descricao) LIKE concat('%', lower(?1), '%') AND p.dataPagamento BETWEEN ?2 AND ?3 GROUP BY p.pagar.categoria.descricao")
    List<Object> somarTotalPagoAgrupadoPorCategoria(String descricao, Date dataInicio, Date dataFim);

    @Query("SELECT SUM(p.valorPago), p.dataPagamento FROM Pago p WHERE lower(p.pagar.descricao) LIKE concat('%', lower(?1), '%') AND p.dataPagamento BETWEEN ?2 AND ?3 GROUP BY p.dataPagamento ORDER BY p.dataPagamento")
    List<Object> somarTotalPagoAgrupadoPorDia(String descricao, Date dataInicio, Date dataFim);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pago WHERE id = :id")
    void excluirPagamentoPorId(@Param("id") Long id);
}
