package controle;

import entidades.Contatos;
import entidades.Endereco;
import entidades.Icrud;
import entidades.Telefone;
import java.util.List;
import persistencia.ContatoDao;
import util.conexaoBD;
import javax.swing.JOptionPane;

public class Controle implements Icrud {

    private Icrud acesso;

    public Controle(Icrud acesso) {
        this.acesso = acesso;
    }

    @Override
    public void incluir(Contatos contato) throws Exception {
        String erro = verificar(contato);
        if (!erro.isEmpty()) {
            JOptionPane.showMessageDialog(null, erro, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;  // Evita que o método continue após encontrar um erro
        }

        try {
            acesso.incluir(contato);
        } catch (Exception e) {
            throw new Exception("Erro ao incluir contato: " + e.getMessage(), e);
        }
    }

    @Override
    public void excluir(String nomeCompleto) throws Exception {
        if (nomeCompleto == null || nomeCompleto.isEmpty()) {
            String erro = "Insira o nome do cliente a ser excluído.\n";
            JOptionPane.showMessageDialog(null, erro, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;  // Evita que o método continue após encontrar um erro
        }

        // Verificar conexão com o banco de dados
        if (conexaoBD.getConexao() == null) {
            throw new Exception("Não foi possível conectar ao banco de dados.");
        }

        acesso.excluir(nomeCompleto);
    }

    @Override
    public void alterar(Contatos contatoNovo) throws Exception {
        String erro = verificar(contatoNovo);
        if (!erro.isEmpty()) {
            JOptionPane.showMessageDialog(null, erro, "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;  // Evita que o método continue após encontrar um erro
        }

        // Verificar conexão com o banco de dados
        if (conexaoBD.getConexao() == null) {
            throw new Exception("Não foi possível conectar ao banco de dados.");
        }

        acesso.alterar(contatoNovo);
    }

    @Override
    public List<Contatos> consultar(String nome) throws Exception {
        // Verificar conexão com o banco de dados
        if (conexaoBD.getConexao() == null) {
            throw new Exception("Não foi possível conectar ao banco de dados.");
        }

        return acesso.consultar(nome);
    }

    @Override
    public List<Contatos> listarContatos(String orderBy) throws Exception {
        // Verificar conexão com o banco de dados
        if (conexaoBD.getConexao() == null) {
            throw new Exception("Não foi possível conectar ao banco de dados.");
        }

        return acesso.listarContatos(orderBy);
    }

    public String verificar(Contatos contato) {
    // Verificação do campo nome completo
    if (contato.getNomeCompleto().isEmpty()) {
        return "Esse campo (nome) é obrigatório, não pode estar vazio.";
    }
    if (!contato.getNomeCompleto().matches("^[a-zA-Z ]+$")) {
        return "Esse campo (nome) somente aceita letras.";
    }

    // Verificação do campo email
    if (contato.getEmail().isEmpty()) {
        return "Esse campo (email) é obrigatório, não pode estar vazio.";
    }
    // Regex robusta para email
    if (!contato.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
        return "Email inválido.";
    }

    // Verificação dos campos de telefone
    Telefone telefone = contato.getTelefone();
    if (telefone == null) {
        return "Telefone não pode ser nulo.";
    } else {
        if (telefone.getDdi() <= 0) {
            return "Esse campo (DDI) é obrigatório e deve ser um número positivo.";
        }
        if (telefone.getDdd() <= 0) {
            return "Esse campo (DDD) é obrigatório e deve ser um número positivo.";
        }
        String numeroStr = String.valueOf(telefone.getNumero());
        if (numeroStr.isEmpty()) {
            return "Esse campo (número) é obrigatório e não pode estar vazio.";
        }
        if (!numeroStr.matches("[0-9]+")) {
            return "Esse campo (número) somente aceita números.";
        }
        if (numeroStr.length() != 9) {
            return "Número inválido!";
        }
    }

    // Verificação dos campos de endereço
    Endereco endereco = contato.getEndereco();
    if (endereco == null) {
        return "Endereço não pode ser nulo.";
    } else {
        if (endereco.getLogradouro().isEmpty()) {
            return "Esse campo (logradouro) é obrigatório, não pode estar vazio.";
        }
        if (endereco.getNumero() <= 0) {
            return "Esse campo (número) deve ser um número positivo.";
        }
        String cepStr = String.valueOf(endereco.getCep());
        if (cepStr.isEmpty()) {
            return "Esse campo (CEP) é obrigatório e não pode estar vazio.";
        }
        if (!cepStr.matches("[0-9]+")) {
            return "Esse campo (CEP) somente aceita números.";
        }
        if (endereco.getCidade().isEmpty()) {
            return "Esse campo (cidade) é obrigatório, não pode estar vazio.";
        }
        if (!endereco.getCidade().matches("^[a-zA-Z ]+$")) {
            return "Esse campo (cidade) somente aceita letras.";
        }
        if (endereco.getEstado().isEmpty()) {
            return "Esse campo (estado) é obrigatório, não pode estar vazio.";
        }
        if (!endereco.getEstado().matches("^[a-zA-Z ]+$")) {
            return "Esse campo (estado) somente aceita letras.";
        }
    }

    return ""; // Nenhum erro encontrado
}
}
