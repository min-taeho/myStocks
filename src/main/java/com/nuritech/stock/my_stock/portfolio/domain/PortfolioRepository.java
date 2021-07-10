package com.nuritech.stock.my_stock.portfolio.domain;

import com.nuritech.stock.my_stock.portfolio.dto.PortfolioListExpandedDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, PortfolioId> {
    @Query("SELECT p FROM Portfolio p")
    List<Portfolio> findAllDesc();

    @Query(value = "SELECT v.ticker, v.stock_nm, business_cycle, sector, " +
                   "       current_price, avg_unit_price, total_stock_num, annual_payout, " +
                   "       highest_price, lower_price, " +
                   "       total_stock_num * annual_payout AS total_payout, " +
                   "       annual_payout / avg_unit_price * 100 AS investment_div_yield, " +
                   "       total_trading_amount, " +
                   "       current_price * total_stock_num AS eval_amount, " +
                   "       (current_price * total_stock_num) - total_trading_amount AS earning_amount, " +
                   "       ((current_price * total_stock_num) - total_trading_amount)/total_trading_amount * 100 AS earning_rate, " +
                   "       total_trading_amount/sum_trading_amount*100 AS protion, " +
                   "       dividend_pay_month " +
                   " FROM (SELECT p.ticker, s.stock_nm, e.business_cycle, e.sector, " +
                   "              s.current_price, e.annual_payout, e.div_yield, " +
                   "              s.dividend_pay_month, s.highest_price, s.lower_price, " +
                   "              SUM(case when d.trading_type='매수' then d.unit_price*d.stock_num ELSE 0 END) / " +
                   "                  SUM(case when d.trading_type='매수' then d.stock_num ELSE 0 END) AS avg_unit_price, " +
                   "              sum(d.stock_num)  AS total_stock_num, " +
                   "              sum(ifnull(d.trading_amount, 0)) AS total_trading_amount " +
                   "              sum(CASE WHEN d.trading_type='매수' then d.stock_num ELSE 0 END) buy_num " +
                   "              sum(CASE WHEN d.trading_type='매도' then d.stock_num ELSE 0 END) sell_num " +
                   "         FROM portfolio p " +
                   "              LEFT OUTER JOIN stock s ON p.ticker = s.ticker " +
                   "              LEFT OUTER JOIN daybooks d ON p.ticker = d.ticker AND p.email = d.email " +
                   "              LEFT OUTER JOIN interested_stock e ON p.ticker = e.ticker AND p.email = e.email " +
                   "        WHERE p.email = :email " +
                   "        GROUP BY p.ticker " +
                   "       ) v " +
                   "       ,(SELECT SUM(trading_amount) AS sum_trading_amount FROM daybooks) t " +
                   "GROUP BY v.ticker " +
                   "ORDER BY business_cycle, sector, ticker ", nativeQuery = true)
    List<Object[]> selectPortfolio(@Param("email") String email);

    @Query(value = "SELECT SUM(v.total_payout) AS total_payout, " +
                   "       SUM(v.total_trading_amount) AS total_trading_amount, " +
                   "       SUM(v.total_eval_amount) AS total_eval_amount, " +
                   "       SUM(v.total_eval_amount) - SUM(v.total_trading_amount) AS earning_amount, " +
                   "       (SUM(v.total_eval_amount) - SUM(v.total_trading_amount)) / SUM(v.total_trading_amount) * 100 AS earning_rate " +
                   "  FROM ( " +
                   "        SELECT p.ticker,  " +
                   "               ( sum(CASE WHEN d.trading_type='매수' then d.stock_num ELSE 0 END) " +
                   "                     - sum(CASE WHEN d.trading_type='매도' then d.stock_num ELSE 0 END) ) * e.annual_payout AS total_payout, " +
                   "               ( sum(CASE WHEN d.trading_type='매수' then d.stock_num ELSE 0 END) " +
                   "                     - sum(CASE WHEN d.trading_type='매도' then d.stock_num ELSE 0 END) ) * d.unit_price  AS total_trading_amount , " +
                   "               ( sum(CASE WHEN d.trading_type='매수' then d.stock_num ELSE 0 END) " +
                   "                     - sum(CASE WHEN d.trading_type='매도' then d.stock_num ELSE 0 END) ) * s.current_price AS total_eval_amount  " +
                   "          FROM portfolio p  " +
                   "               LEFT OUTER JOIN stock s ON p.ticker = s.ticker  " +
                   "               LEFT OUTER JOIN daybooks d ON p.ticker = d.ticker AND p.email = d.email  " +
                   "               LEFT OUTER JOIN interested_stock e ON p.ticker = e.ticker AND p.email = e.email" +
                   "         WHERE p.email = :email " +
                   "         GROUP BY p.ticker " +
                   "       ) v", nativeQuery = true)
    List<Object[]> selectSumPortfolio(@Param("email") String email);

    @Query(value =  "SELECT s.dividend_pay_month, " +
                    "       ( sum(CASE WHEN d.trading_type='매수' then d.stock_num ELSE 0 END) " +
                    "             - sum(CASE WHEN d.trading_type='매도' then d.stock_num ELSE 0 END) ) * e.annual_payout AS total_payout, " +
                    "       (( sum(CASE WHEN d.trading_type='매수' then d.stock_num ELSE 0 END) " +
                    "              - sum(CASE WHEN d.trading_type='매도' then d.stock_num ELSE 0 END) ) * e.annual_payout) " +
                    "        / CASE WHEN e.dividend_pay_month='per months' THEN 12 ELSE 4 END AS payout_month " +
                    "  FROM portfolio p " +
                    "       LEFT OUTER JOIN stock s ON p.ticker = s.ticker " +
                    "       LEFT OUTER JOIN daybooks d ON p.ticker = d.ticker AND p.email = d.email " +
                    "       LEFT OUTER JOIN interested_stock e ON p.ticker = e.ticker AND p.email = e.email " +
                    " WHERE p.email = :email " +
                    "   AND s.dividend_pay_month != '' " +
                    " GROUP BY s.dividend_pay_month ", nativeQuery = true)
    List<Object[]> selectSummaryPayoutPerMonth(@Param("email") String email);

}
