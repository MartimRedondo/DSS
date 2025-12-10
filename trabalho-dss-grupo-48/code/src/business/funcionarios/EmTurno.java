package business.funcionarios;

import business.workshop.Horario;

import java.time.LocalDateTime;
import java.util.Objects;

public class EmTurno {
    private boolean emTurno;
    private Posto_Trabalho posto;

    private Horario horario;

    private FazerServico fazerServico;

    public EmTurno(boolean emTurno, Posto_Trabalho posto, Horario horario, FazerServico fazerServico) {
        this.emTurno = emTurno;
        this.posto = posto;
        this.horario = horario;
        this.fazerServico = fazerServico;
    }

    public boolean isEmTurno() {
        return emTurno;
    }

    public void setEmTurno(boolean emTurno) {
        this.emTurno = emTurno;
    }

    public Posto_Trabalho getPosto() {
        return posto;
    }

    public void setPosto(Posto_Trabalho posto) {
        this.posto = posto;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public void setInicioH(LocalDateTime inicio){
        this.horario.setInicio(inicio);
    }

    public void setFimH(LocalDateTime fim){
        this.horario.setFim(fim);
    }

    public FazerServico getFazerServico() {
        return fazerServico;
    }

    public void setFazerServico(FazerServico fazerServico) {
        this.fazerServico = fazerServico;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmTurno emTurno1)) return false;
        return emTurno == emTurno1.emTurno && Objects.equals(posto, emTurno1.posto) && Objects.equals(horario, emTurno1.horario) && Objects.equals(fazerServico, emTurno1.fazerServico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emTurno, posto, horario, fazerServico);
    }
}
