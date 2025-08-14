package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.application.dto.fiscal.NFeXmlData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NFeXmlParserTest {
    
    private NFeXmlParser parser;
    
    @BeforeEach
    void setUp() {
        parser = new NFeXmlParser();
    }
    
    @Test
    void testParseNFeXmlBasico() {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                            <serie>1</serie>
                            <dhEmi>2024-01-15T10:00:00-03:00</dhEmi>
                        </ide>
                        <emit>
                            <CNPJ>11222333000144</CNPJ>
                            <xNome>Fornecedor Teste LTDA</xNome>
                            <xFant>Fornecedor Teste</xFant>
                            <IE>123456789</IE>
                            <enderEmit>
                                <xLgr>Rua do Fornecedor</xLgr>
                                <nro>123</nro>
                                <xBairro>Centro</xBairro>
                                <xMun>São Paulo</xMun>
                                <UF>SP</UF>
                                <CEP>01234567</CEP>
                            </enderEmit>
                        </emit>
                        <dest>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>FreePharma Matriz</xNome>
                            <enderDest>
                                <xLgr>Rua das Flores</xLgr>
                                <nro>123</nro>
                                <xBairro>Centro</xBairro>
                                <xMun>São Paulo</xMun>
                                <UF>SP</UF>
                            </enderDest>
                        </dest>
                        <det nItem="1">
                            <prod>
                                <cProd>PARACETAMOL500</cProd>
                                <cEAN>7891234567890</cEAN>
                                <xProd>Paracetamol 500mg cx 20 comp</xProd>
                                <NCM>30049099</NCM>
                                <CFOP>1102</CFOP>
                                <uCom>CX</uCom>
                                <qCom>10</qCom>
                                <vUnCom>15.50</vUnCom>
                                <vProd>155.00</vProd>
                            </prod>
                        </det>
                        <det nItem="2">
                            <prod>
                                <cProd>DIPIRONA500</cProd>
                                <cEAN>7891234567891</cEAN>
                                <xProd>Dipirona 500mg cx 10 comp</xProd>
                                <NCM>30049050</NCM>
                                <CFOP>1102</CFOP>
                                <uCom>CX</uCom>
                                <qCom>20</qCom>
                                <vUnCom>8.75</vUnCom>
                                <vProd>175.00</vProd>
                            </prod>
                        </det>
                        <total>
                            <ICMSTot>
                                <vNF>330.00</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlContent.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        // Verificar dados principais
        assertNotNull(result);
        assertEquals("35240111222333000144550010000012341234567890", result.getChaveAcesso());
        assertEquals("1234", result.getNumero());
        assertEquals("1", result.getSerie());
        assertEquals(new BigDecimal("330.00"), result.getValorTotal());
        assertEquals("COMPRA", result.getTipoOperacao()); // CFOP 1102 = compra
        
        // Verificar emitente
        NFeXmlData.EmitenteDados emitente = result.getEmitente();
        assertNotNull(emitente);
        assertEquals("11222333000144", emitente.getCnpj());
        assertEquals("Fornecedor Teste LTDA", emitente.getRazaoSocial());
        assertEquals("Fornecedor Teste", emitente.getNomeFantasia());
        assertTrue(emitente.getEndereco().contains("Rua do Fornecedor"));
        assertTrue(emitente.getEndereco().contains("São Paulo"));
        
        // Verificar destinatário
        NFeXmlData.DestinatarioDados destinatario = result.getDestinatario();
        assertNotNull(destinatario);
        assertEquals("12345678000199", destinatario.getCnpjCpf());
        assertEquals("FreePharma Matriz", destinatario.getNome());
        
        // Verificar itens
        assertEquals(2, result.getItens().size());
        
        NFeXmlData.ItemNFeDados item1 = result.getItens().get(0);
        assertEquals("PARACETAMOL500", item1.getCodigoProduto());
        assertEquals("Paracetamol 500mg cx 20 comp", item1.getNomeProduto());
        assertEquals("7891234567890", item1.getEan());
        assertEquals("30049099", item1.getNcm());
        assertEquals("1102", item1.getCfop());
        assertEquals("CX", item1.getUnidadeMedida());
        assertEquals(10, item1.getQuantidade());
        assertEquals(new BigDecimal("15.50"), item1.getValorUnitario());
        assertEquals(new BigDecimal("155.00"), item1.getValorTotal());
        
        NFeXmlData.ItemNFeDados item2 = result.getItens().get(1);
        assertEquals("DIPIRONA500", item2.getCodigoProduto());
        assertEquals(20, item2.getQuantidade());
        assertEquals(new BigDecimal("8.75"), item2.getValorUnitario());
        assertEquals(new BigDecimal("175.00"), item2.getValorTotal());
    }
    
    @Test
    void testDeterminaTipoOperacaoVenda() {
        String xmlContent = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                            <serie>1</serie>
                            <dhEmi>2024-01-15T10:00:00-03:00</dhEmi>
                        </ide>
                        <emit>
                            <CNPJ>11222333000144</CNPJ>
                            <xNome>Farmacia Vendedora LTDA</xNome>
                        </emit>
                        <dest>
                            <CPF>12345678901</CPF>
                            <xNome>Cliente Comprador</xNome>
                        </dest>
                        <det nItem="1">
                            <prod>
                                <cProd>PRODUTO001</cProd>
                                <xProd>Produto Teste</xProd>
                                <CFOP>5102</CFOP>
                                <qCom>1</qCom>
                                <vUnCom>10.00</vUnCom>
                                <vProd>10.00</vProd>
                            </prod>
                        </det>
                        <total>
                            <ICMSTot>
                                <vNF>10.00</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlContent.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        assertEquals("VENDA", result.getTipoOperacao()); // CFOP 5102 = venda
        
        // Verificar que o destinatário tem CPF (não CNPJ)
        assertEquals("12345678901", result.getDestinatario().getCnpjCpf());
    }
    
    @Test
    void testXmlInvalido() {
        String xmlInvalido = "<xml>conteudo inválido</xml>";
        byte[] xmlBytes = xmlInvalido.getBytes();
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            parser.parseNFeXml(xmlBytes);
        });
        
        assertTrue(exception.getMessage().contains("Erro ao fazer parse do XML"));
    }
    
    @Test
    void testXmlSemElementoNFe() {
        String xmlSemNFe = """
            <?xml version="1.0" encoding="UTF-8"?>
            <root>
                <outras>dados</outras>
            </root>
            """;
        
        byte[] xmlBytes = xmlSemNFe.getBytes();
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            parser.parseNFeXml(xmlBytes);
        });
        
        assertTrue(exception.getMessage().contains("Elemento NFe não encontrado"));
    }
    
    @Test
    void testParseComValoresNulos() {
        String xmlMinimo = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                        </ide>
                        <emit>
                            <CNPJ>11222333000144</CNPJ>
                            <xNome>Fornecedor</xNome>
                        </emit>
                        <dest>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>Destinatario</xNome>
                        </dest>
                        <total>
                            <ICMSTot>
                                <vNF>100.00</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlMinimo.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        // Deve funcionar mesmo com campos opcionais ausentes
        assertNotNull(result);
        assertEquals("1234", result.getNumero());
        assertEquals(new BigDecimal("100.00"), result.getValorTotal());
        assertEquals(0, result.getItens().size()); // Sem itens
        assertEquals("COMPRA", result.getTipoOperacao()); // Default quando não há itens
    }
    
    @Test
    void testParseComLoteEDataVencimento() {
        String xmlComLote = """
            <?xml version="1.0" encoding="UTF-8"?>
            <nfeProc xmlns="http://www.portalfiscal.inf.br/nfe">
                <NFe>
                    <infNFe Id="NFe35240111222333000144550010000012341234567890">
                        <ide>
                            <nNF>1234</nNF>
                        </ide>
                        <emit>
                            <CNPJ>11222333000144</CNPJ>
                            <xNome>Fornecedor</xNome>
                        </emit>
                        <dest>
                            <CNPJ>12345678000199</CNPJ>
                            <xNome>Destinatario</xNome>
                        </dest>
                        <det nItem="1">
                            <prod>
                                <cProd>MEDICAMENTO001</cProd>
                                <xProd>Medicamento com lote</xProd>
                                <CFOP>1102</CFOP>
                                <qCom>5</qCom>
                                <vUnCom>20.00</vUnCom>
                                <vProd>100.00</vProd>
                                <xLote>LOTE123</xLote>
                                <dVal>2025-12-31</dVal>
                            </prod>
                        </det>
                        <total>
                            <ICMSTot>
                                <vNF>100.00</vNF>
                            </ICMSTot>
                        </total>
                    </infNFe>
                </NFe>
            </nfeProc>
            """;
        
        byte[] xmlBytes = xmlComLote.getBytes();
        NFeXmlData result = parser.parseNFeXml(xmlBytes);
        
        assertEquals(1, result.getItens().size());
        NFeXmlData.ItemNFeDados item = result.getItens().get(0);
        assertEquals("LOTE123", item.getLote());
        assertNotNull(item.getDataVencimento());
    }
}