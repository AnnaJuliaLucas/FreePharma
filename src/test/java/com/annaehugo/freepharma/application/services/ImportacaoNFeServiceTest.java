package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.application.dto.fiscal.NFeXmlData;
import com.annaehugo.freepharma.domain.entity.fiscal.ImportacaoNFe;
import com.annaehugo.freepharma.domain.entity.fiscal.StatusImportacao;
import com.annaehugo.freepharma.domain.entity.fiscal.NotaFiscal;
import com.annaehugo.freepharma.domain.entity.estoque.Fornecedor;
import com.annaehugo.freepharma.domain.repository.fiscal.ImportacaoNFeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportacaoNFeServiceTest {

    @Mock(lenient = true)
    private NFeXmlParser nfeXmlParser;
    
    @Mock(lenient = true)
    private ProcessamentoNFeService processamentoNFeService;
    
    @Mock(lenient = true)
    private ImportacaoNFeRepository importacaoNFeRepository;

    @InjectMocks
    private ImportacaoNFeService importacaoNFeService;

    private MockMultipartFile xmlFile;
    private MockMultipartFile emptyFile;
    private MockMultipartFile nonXmlFile;

    @BeforeEach
    void setUp() {
        // Configurar mocks
        ImportacaoNFe importacao = new ImportacaoNFe();
        importacao.setId(1L);
        importacao.setStatus(StatusImportacao.PENDENTE);
        when(importacaoNFeRepository.save(any(ImportacaoNFe.class))).thenReturn(importacao);
        
        // Configurar NFe data mock
        NFeXmlData nfeData = new NFeXmlData();
        nfeData.setChaveAcesso("12345678901234567890123456789012345678901234"); // 44 caracteres
        nfeData.setNumero("123");
        nfeData.setValorTotal(new BigDecimal("100.00"));
        nfeData.setDataEmissao(new Date());
        
        // Configurar emitente
        NFeXmlData.EmitenteDados emitente = new NFeXmlData.EmitenteDados();
        emitente.setCnpj("12345678000199");
        emitente.setRazaoSocial("Fornecedor Teste Ltda");
        nfeData.setEmitente(emitente);
        
        // Configurar pelo menos um item
        NFeXmlData.ItemNFeDados item = new NFeXmlData.ItemNFeDados();
        item.setCodigoProduto("PROD001");
        item.setNomeProduto("Produto Teste");
        item.setQuantidade(1);
        item.setValorUnitario(new BigDecimal("100.00"));
        item.setValorTotal(new BigDecimal("100.00"));
        nfeData.getItens().add(item);
        
        when(nfeXmlParser.parseNFeXml(any(byte[].class))).thenReturn(nfeData);
        
        // Configurar resultado de processamento mock
        ProcessamentoNFeService.ProcessamentoResult resultado = new ProcessamentoNFeService.ProcessamentoResult();
        resultado.setSucesso(true);
        resultado.setMensagem("NFe processada com sucesso. 1 itens processados.");
        
        // Configurar nota fiscal mock
        NotaFiscal notaFiscal = new NotaFiscal();
        notaFiscal.setId(1L);
        resultado.setNotaFiscal(notaFiscal);
        
        // Configurar fornecedor mock
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setId(1L);
        resultado.setFornecedor(fornecedor);
        
        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(), any(ImportacaoNFe.class)))
            .thenReturn(resultado);
        
        // Arquivo XML válido
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><NFe><infNFe><ide><nNF>123</nNF></ide></infNFe></NFe>";
        xmlFile = new MockMultipartFile(
            "file", 
            "nota_fiscal.xml", 
            "text/xml", 
            xmlContent.getBytes()
        );

        // Arquivo vazio
        emptyFile = new MockMultipartFile(
            "file", 
            "empty.xml", 
            "text/xml", 
            new byte[0]
        );

        // Arquivo não XML
        nonXmlFile = new MockMultipartFile(
            "file", 
            "documento.pdf", 
            "application/pdf", 
            "conteúdo não xml".getBytes()
        );
    }

    @Test
    void importarNFeXml_ComArquivoXmlValido_DeveRetornarSucesso() {
        // When
        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFile);

        // Then
        assertNotNull(resultado);
        assertEquals("SUCESSO", resultado.get("status"));
        assertEquals("nota_fiscal.xml", resultado.get("arquivo"));
        assertEquals(xmlFile.getSize(), resultado.get("tamanho"));
        assertTrue(resultado.get("mensagem").toString().contains("processada com sucesso"));
    }

    @Test
    void importarNFeXml_ComArquivoVazio_DeveLancarExcecao() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(emptyFile));
        assertEquals("Arquivo XML não pode ser vazio", exception.getMessage());
    }

    @Test
    void importarNFeXml_ComArquivoNaoXml_DeveLancarExcecao() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(nonXmlFile));
        assertEquals("Apenas arquivos XML são aceitos", exception.getMessage());
    }

    @Test
    void importarNFeXml_ComArquivoXmlMinusculo_DeveAceitarExtensao() {
        // Given
        MockMultipartFile xmlMinusculo = new MockMultipartFile(
            "file", 
            "arquivo.xml", 
            "text/xml", 
            "<?xml version=\"1.0\"?><root></root>".getBytes()
        );

        // When
        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlMinusculo);

        // Then
        assertNotNull(resultado);
        assertEquals("SUCESSO", resultado.get("status"));
        assertEquals("arquivo.xml", resultado.get("arquivo"));
    }

    @Test
    void importarNFeXml_ComArquivoXmlMaiusculo_DeveAceitarExtensao() {
        // Given
        MockMultipartFile xmlMaiusculo = new MockMultipartFile(
            "file", 
            "ARQUIVO.XML", 
            "text/xml", 
            "<?xml version=\"1.0\"?><root></root>".getBytes()
        );

        // When
        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlMaiusculo);

        // Then
        assertNotNull(resultado);
        assertEquals("SUCESSO", resultado.get("status"));
        assertEquals("ARQUIVO.XML", resultado.get("arquivo"));
    }

    @Test
    void importarNFeXml_ComArquivoSemExtensao_DeveLancarExcecao() {
        // Given
        MockMultipartFile arquivoSemExtensao = new MockMultipartFile(
            "file", 
            "arquivo", 
            "text/xml", 
            "<?xml version=\"1.0\"?><root></root>".getBytes()
        );

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(arquivoSemExtensao));
        assertEquals("Apenas arquivos XML são aceitos", exception.getMessage());
    }

    @Test
    void importarNFeXml_ComExtensaoIncorreta_DeveLancarExcecao() {
        // Given
        MockMultipartFile arquivoTxt = new MockMultipartFile(
            "file", 
            "arquivo.txt", 
            "text/plain", 
            "conteúdo texto".getBytes()
        );

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(arquivoTxt));
        assertEquals("Apenas arquivos XML são aceitos", exception.getMessage());
    }

    @Test
    void importarNFeXml_ComNomeArquivoNulo_DeveLancarExcecao() {
        // Given
        MockMultipartFile arquivoSemNome = new MockMultipartFile(
            "file", 
            null, 
            "text/xml", 
            "<?xml version=\"1.0\"?><root></root>".getBytes()
        );

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(arquivoSemNome));
        assertEquals("Nome do arquivo é obrigatório", exception.getMessage());
    }
    
    @Test
    void importarNFeXml_ComArquivoMuitoGrande_DeveLancarExcecao() {
        // Given - arquivo de 11MB (maior que o limite de 10MB)
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile arquivoGrande = new MockMultipartFile(
            "file", 
            "arquivo_grande.xml", 
            "text/xml", 
            largeContent
        );

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(arquivoGrande));
        assertTrue(exception.getMessage().contains("Arquivo muito grande"));
    }
    
    @Test
    void importarNFeXml_ComXmlInvalidoSemNFe_DeveLancarExcecao() {
        // Given
        String xmlInvalido = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><data>test</data></root>";
        MockMultipartFile arquivoXmlInvalido = new MockMultipartFile(
            "file", 
            "xml_invalido.xml", 
            "text/xml", 
            xmlInvalido.getBytes()
        );

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(arquivoXmlInvalido));
        assertTrue(exception.getMessage().contains("XML não contém estrutura de NFe válida"));
    }
    
    @Test
    void importarNFeXml_ComChaveAcessoInvalida_DeveLancarExcecao() {
        // Given - NFe com chave de acesso inválida
        NFeXmlData nfeDataInvalida = new NFeXmlData();
        nfeDataInvalida.setChaveAcesso("123"); // Menos que 44 caracteres
        nfeDataInvalida.setNumero("123");
        nfeDataInvalida.setValorTotal(new BigDecimal("100.00"));
        
        when(nfeXmlParser.parseNFeXml(any(byte[].class))).thenReturn(nfeDataInvalida);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(xmlFile));
        assertEquals("Chave de acesso inválida", exception.getMessage());
    }
    
    @Test
    void importarNFeXml_ComValorTotalZero_DeveLancarExcecao() {
        // Given - NFe com valor zero
        NFeXmlData nfeDataInvalida = new NFeXmlData();
        nfeDataInvalida.setChaveAcesso("12345678901234567890123456789012345678901234");
        nfeDataInvalida.setNumero("123");
        nfeDataInvalida.setValorTotal(BigDecimal.ZERO);
        
        // Adicionar emitente obrigatório
        NFeXmlData.EmitenteDados emitente = new NFeXmlData.EmitenteDados();
        emitente.setCnpj("12345678000199");
        nfeDataInvalida.setEmitente(emitente);
        
        when(nfeXmlParser.parseNFeXml(any(byte[].class))).thenReturn(nfeDataInvalida);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(xmlFile));
        assertEquals("Valor total da NFe deve ser maior que zero", exception.getMessage());
    }
    
    @Test
    void importarNFeXml_SemEmitente_DeveLancarExcecao() {
        // Given - NFe sem emitente
        NFeXmlData nfeDataInvalida = new NFeXmlData();
        nfeDataInvalida.setChaveAcesso("12345678901234567890123456789012345678901234");
        nfeDataInvalida.setNumero("123");
        nfeDataInvalida.setValorTotal(new BigDecimal("100.00"));
        // Emitente = null
        
        when(nfeXmlParser.parseNFeXml(any(byte[].class))).thenReturn(nfeDataInvalida);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(xmlFile));
        assertEquals("Dados do emitente são obrigatórios", exception.getMessage());
    }
    
    @Test
    void importarNFeXml_SemItens_DeveLancarExcecao() {
        // Given - NFe sem itens
        NFeXmlData nfeDataInvalida = new NFeXmlData();
        nfeDataInvalida.setChaveAcesso("12345678901234567890123456789012345678901234");
        nfeDataInvalida.setNumero("123");
        nfeDataInvalida.setValorTotal(new BigDecimal("100.00"));
        
        NFeXmlData.EmitenteDados emitente = new NFeXmlData.EmitenteDados();
        emitente.setCnpj("12345678000199");
        nfeDataInvalida.setEmitente(emitente);
        // Lista de itens vazia
        
        when(nfeXmlParser.parseNFeXml(any(byte[].class))).thenReturn(nfeDataInvalida);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> importacaoNFeService.importarNFeXml(xmlFile));
        assertEquals("NFe deve conter pelo menos um item", exception.getMessage());
    }
    
    @Test
    void importarNFeXml_ComErroProcessamento_DeveRetornarErro() {
        // Given
        ProcessamentoNFeService.ProcessamentoResult resultadoErro = new ProcessamentoNFeService.ProcessamentoResult();
        resultadoErro.setSucesso(false);
        resultadoErro.setMensagem("Erro no processamento");
        resultadoErro.getErros().add("Erro específico no produto");
        
        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(), any(ImportacaoNFe.class)))
            .thenReturn(resultadoErro);

        // When
        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFile);

        // Then
        assertNotNull(resultado);
        assertEquals("ERRO", resultado.get("status"));
        assertEquals("Erro no processamento", resultado.get("mensagem"));
        assertTrue(resultado.get("erros") != null);
    }
    
    @Test
    void importarNFeXml_ComInconsistenciasDetectadas_DeveIncluirAlertas() {
        // Given - resultado com sucesso mas com inconsistências
        ProcessamentoNFeService.ProcessamentoResult resultadoComInconsistencias = new ProcessamentoNFeService.ProcessamentoResult();
        resultadoComInconsistencias.setSucesso(true);
        resultadoComInconsistencias.setMensagem("NFe processada com sucesso. 1 itens processados.");
        
        when(processamentoNFeService.processarNFe(any(NFeXmlData.class), any(), any(ImportacaoNFe.class)))
            .thenReturn(resultadoComInconsistencias);
        
        // Simular inconsistências detectadas
        ImportacaoNFe importacaoComInconsistencia = new ImportacaoNFe();
        importacaoComInconsistencia.setId(1L);
        importacaoComInconsistencia.setStatus(StatusImportacao.CONCLUIDA);
        importacaoComInconsistencia.setQuantidadeInconsistenciasDetectadas(3);
        
        when(importacaoNFeRepository.save(any(ImportacaoNFe.class))).thenReturn(importacaoComInconsistencia);

        // When
        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFile);

        // Then
        assertEquals("SUCESSO", resultado.get("status"));
        assertTrue(resultado.get("mensagem").toString().contains("processada com sucesso"));
    }

    @Test
    void importarNFeXml_VerificarEstruturaDaResposta() {
        // When
        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(xmlFile);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.containsKey("status"));
        assertTrue(resultado.containsKey("arquivo"));
        assertTrue(resultado.containsKey("tamanho"));
        assertTrue(resultado.containsKey("mensagem"));
    }

    @Test
    void importarNFeXml_DeveRetornarTamanhoCorreto() {
        // Given
        String conteudo = "<?xml version=\"1.0\"?><NFe><infNFe><ide><nNF>123</nNF></ide></infNFe></NFe>";
        MockMultipartFile arquivo = new MockMultipartFile(
            "file", 
            "teste.xml", 
            "text/xml", 
            conteudo.getBytes()
        );

        // When
        Map<String, Object> resultado = importacaoNFeService.importarNFeXml(arquivo);

        // Then
        assertEquals((long)conteudo.getBytes().length, resultado.get("tamanho"));
    }
}