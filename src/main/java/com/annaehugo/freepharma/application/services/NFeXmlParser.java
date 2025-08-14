package com.annaehugo.freepharma.application.services;

import com.annaehugo.freepharma.application.dto.fiscal.NFeXmlData;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class NFeXmlParser {
    
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    
    public NFeXmlData parseNFeXml(byte[] xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent));
            
            document.getDocumentElement().normalize();
            
            NFeXmlData nfeData = new NFeXmlData();
            
            // Buscar elemento principal da NFe
            Element nfeElement = getElementByTagName(document, "NFe");
            if (nfeElement == null) {
                throw new RuntimeException("Elemento NFe não encontrado no XML");
            }
            
            Element infNFeElement = getElementByTagName(nfeElement, "infNFe");
            if (infNFeElement == null) {
                throw new RuntimeException("Elemento infNFe não encontrado no XML");
            }
            
            // Extrair dados principais da NFe
            extractNFeMainData(nfeData, infNFeElement);
            
            // Extrair dados do emitente (fornecedor)
            extractEmitenteData(nfeData, infNFeElement);
            
            // Extrair dados do destinatário
            extractDestinatarioData(nfeData, infNFeElement);
            
            // Extrair itens da nota
            extractItensData(nfeData, infNFeElement);
            
            // Determinar tipo de operação baseado no CFOP
            determineOperationType(nfeData);
            
            return nfeData;
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer parse do XML da NFe: " + e.getMessage(), e);
        }
    }
    
    private void extractNFeMainData(NFeXmlData nfeData, Element infNFeElement) {
        // Chave de acesso
        String chave = infNFeElement.getAttribute("Id");
        if (chave != null && chave.startsWith("NFe")) {
            chave = chave.substring(3); // Remove "NFe" prefix
        }
        nfeData.setChaveAcesso(chave);
        
        // Dados básicos da NFe
        Element ideElement = getElementByTagName(infNFeElement, "ide");
        if (ideElement != null) {
            nfeData.setNumero(getTextContent(ideElement, "nNF"));
            nfeData.setSerie(getTextContent(ideElement, "serie"));
            
            // Data de emissão
            String dataEmissaoStr = getTextContent(ideElement, "dhEmi");
            if (dataEmissaoStr == null || dataEmissaoStr.isEmpty()) {
                dataEmissaoStr = getTextContent(ideElement, "dEmi");
            }
            nfeData.setDataEmissao(parseDate(dataEmissaoStr));
        }
        
        // Totais da NFe
        Element totalElement = getElementByTagName(infNFeElement, "total");
        if (totalElement != null) {
            Element icmsTotElement = getElementByTagName(totalElement, "ICMSTot");
            if (icmsTotElement != null) {
                String valorTotalStr = getTextContent(icmsTotElement, "vNF");
                nfeData.setValorTotal(parseBigDecimal(valorTotalStr));
            }
        }
    }
    
    private void extractEmitenteData(NFeXmlData nfeData, Element infNFeElement) {
        Element emitElement = getElementByTagName(infNFeElement, "emit");
        if (emitElement == null) return;
        
        NFeXmlData.EmitenteDados emitente = new NFeXmlData.EmitenteDados();
        
        emitente.setCnpj(getTextContent(emitElement, "CNPJ"));
        emitente.setRazaoSocial(getTextContent(emitElement, "xNome"));
        emitente.setNomeFantasia(getTextContent(emitElement, "xFant"));
        emitente.setInscricaoEstadual(getTextContent(emitElement, "IE"));
        
        // Endereço
        Element endEmitElement = getElementByTagName(emitElement, "enderEmit");
        if (endEmitElement != null) {
            StringBuilder endereco = new StringBuilder();
            appendIfNotNull(endereco, getTextContent(endEmitElement, "xLgr"), "");
            appendIfNotNull(endereco, getTextContent(endEmitElement, "nro"), ", ");
            appendIfNotNull(endereco, getTextContent(endEmitElement, "xBairro"), " - ");
            appendIfNotNull(endereco, getTextContent(endEmitElement, "xMun"), ", ");
            appendIfNotNull(endereco, getTextContent(endEmitElement, "UF"), " - ");
            appendIfNotNull(endereco, getTextContent(endEmitElement, "CEP"), " CEP: ");
            emitente.setEndereco(endereco.toString());
        }
        
        // Contato
        emitente.setTelefone(getTextContent(emitElement, "fone"));
        emitente.setEmail(getTextContent(emitElement, "email"));
        
        nfeData.setEmitente(emitente);
    }
    
    private void extractDestinatarioData(NFeXmlData nfeData, Element infNFeElement) {
        Element destElement = getElementByTagName(infNFeElement, "dest");
        if (destElement == null) return;
        
        NFeXmlData.DestinatarioDados destinatario = new NFeXmlData.DestinatarioDados();
        
        // CNPJ ou CPF
        String cnpj = getTextContent(destElement, "CNPJ");
        String cpf = getTextContent(destElement, "CPF");
        destinatario.setCnpjCpf(cnpj != null ? cnpj : cpf);
        
        destinatario.setNome(getTextContent(destElement, "xNome"));
        
        // Endereço
        Element endDestElement = getElementByTagName(destElement, "enderDest");
        if (endDestElement != null) {
            StringBuilder endereco = new StringBuilder();
            appendIfNotNull(endereco, getTextContent(endDestElement, "xLgr"), "");
            appendIfNotNull(endereco, getTextContent(endDestElement, "nro"), ", ");
            appendIfNotNull(endereco, getTextContent(endDestElement, "xBairro"), " - ");
            appendIfNotNull(endereco, getTextContent(endDestElement, "xMun"), ", ");
            appendIfNotNull(endereco, getTextContent(endDestElement, "UF"), " - ");
            destinatario.setEndereco(endereco.toString());
        }
        
        nfeData.setDestinatario(destinatario);
    }
    
    private void extractItensData(NFeXmlData nfeData, Element infNFeElement) {
        NodeList detNodes = infNFeElement.getElementsByTagName("det");
        
        for (int i = 0; i < detNodes.getLength(); i++) {
            Node detNode = detNodes.item(i);
            if (detNode.getNodeType() == Node.ELEMENT_NODE) {
                Element detElement = (Element) detNode;
                
                Element prodElement = getElementByTagName(detElement, "prod");
                if (prodElement == null) continue;
                
                NFeXmlData.ItemNFeDados item = new NFeXmlData.ItemNFeDados();
                
                item.setCodigoProduto(getTextContent(prodElement, "cProd"));
                item.setNomeProduto(getTextContent(prodElement, "xProd"));
                item.setDescricaoProduto(getTextContent(prodElement, "xProd")); // Usar mesmo valor para descrição
                item.setEan(getTextContent(prodElement, "cEAN"));
                item.setNcm(getTextContent(prodElement, "NCM"));
                item.setCfop(getTextContent(prodElement, "CFOP"));
                item.setUnidadeMedida(getTextContent(prodElement, "uCom"));
                
                // Quantidades e valores
                String qtdStr = getTextContent(prodElement, "qCom");
                item.setQuantidade(qtdStr != null ? Integer.valueOf(qtdStr.split("\\.")[0]) : 0);
                
                item.setValorUnitario(parseBigDecimal(getTextContent(prodElement, "vUnCom")));
                item.setValorTotal(parseBigDecimal(getTextContent(prodElement, "vProd")));
                
                // Dados adicionais (quando disponíveis)
                item.setLote(getTextContent(prodElement, "xLote"));
                String dataVencStr = getTextContent(prodElement, "dVal");
                if (dataVencStr != null) {
                    item.setDataVencimento(parseDate(dataVencStr));
                }
                
                nfeData.getItens().add(item);
            }
        }
    }
    
    private void determineOperationType(NFeXmlData nfeData) {
        // Análise baseada no CFOP predominante dos itens
        // CFOPs de entrada (compra): 1xxx, 2xxx, 3xxx
        // CFOPs de saída (venda): 5xxx, 6xxx, 7xxx
        
        if (nfeData.getItens().isEmpty()) {
            nfeData.setTipoOperacao("COMPRA"); // Default
            return;
        }
        
        String primeiroItem = nfeData.getItens().get(0).getCfop();
        if (primeiroItem != null && primeiroItem.length() >= 1) {
            char firstDigit = primeiroItem.charAt(0);
            if (firstDigit == '5' || firstDigit == '6' || firstDigit == '7') {
                nfeData.setTipoOperacao("VENDA");
            } else {
                nfeData.setTipoOperacao("COMPRA");
            }
        } else {
            nfeData.setTipoOperacao("COMPRA"); // Default
        }
    }
    
    // Métodos utilitários
    private Element getElementByTagName(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? (Element) nodes.item(0) : null;
    }
    
    private Element getElementByTagName(Document document, String tagName) {
        NodeList nodes = document.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? (Element) nodes.item(0) : null;
    }
    
    private String getTextContent(Element parent, String tagName) {
        Element element = getElementByTagName(parent, tagName);
        return element != null ? element.getTextContent() : null;
    }
    
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Tentar formato com timezone primeiro
            if (dateStr.contains("T") && (dateStr.contains("+") || dateStr.contains("Z"))) {
                return dateTimeFormatter.parse(dateStr);
            }
            // Tentar formato de data simples
            return dateFormatter.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    private void appendIfNotNull(StringBuilder sb, String value, String prefix) {
        if (value != null && !value.trim().isEmpty()) {
            if (sb.length() > 0 && !prefix.isEmpty()) {
                sb.append(prefix);
            }
            sb.append(value);
        }
    }
}