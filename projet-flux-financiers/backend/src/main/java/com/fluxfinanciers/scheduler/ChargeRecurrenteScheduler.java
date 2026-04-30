package com.fluxfinanciers.scheduler;

import com.fluxfinanciers.entity.ChargeRecurrente;
import com.fluxfinanciers.entity.PaiementCharge;
import com.fluxfinanciers.enums.ModePaiement;
import com.fluxfinanciers.repository.ChargeRecurrenteRepository;
import com.fluxfinanciers.repository.PaiementChargeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChargeRecurrenteScheduler {

    private final ChargeRecurrenteRepository chargeRecurrenteRepository;
    private final PaiementChargeRepository paiementChargeRepository;

    // Exécution le 1er de chaque mois à 00:05
    @Scheduled(cron = "0 5 0 1 * *")
    @Transactional
    public void genererPaiementsRecurrents() {
        LocalDate today = LocalDate.now();
        LocalDate debutMois = today.withDayOfMonth(1);
        LocalDate finMois = today.withDayOfMonth(today.lengthOfMonth());

        List<ChargeRecurrente> actives = chargeRecurrenteRepository.findByActifTrue();
        int generated = 0;

        for (ChargeRecurrente cr : actives) {
            boolean exists = paiementChargeRepository.existsByCharge_IdAndDateBetween(
                    cr.getCharge().getId(), debutMois, finMois);

            if (!exists) {
                PaiementCharge paiement = new PaiementCharge();
                paiement.setCharge(cr.getCharge());
                paiement.setMontant(cr.getMontantDefaut());
                paiement.setDate(today);
                paiement.setModePaiement(ModePaiement.VIREMENT);
                paiement.setRemarque("Généré automatiquement (charge récurrente)");
                paiementChargeRepository.save(paiement);
                generated++;
                log.info("Paiement récurrent généré : {} — {}€", cr.getCharge().getLibelle(), cr.getMontantDefaut());
            }
        }

        log.info("Charges récurrentes : {} paiement(s) généré(s) pour {}/{}", generated, today.getMonthValue(), today.getYear());
    }
}