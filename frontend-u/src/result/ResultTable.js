import { Table } from "react-bootstrap";
import ResultData from "./ResultData";

function ResultTable() {
    return (
        <Table>
            <Table.Header>
                <Table.Row>
                    <Table.HeaderCell>#</Table.HeaderCell>
                    <Table.HeaderCell>Title</Table.HeaderCell>
                    <Table.HeaderCell>Length</Table.HeaderCell>
                </Table.Row>
            </Table.Header>
            <Table.Body>
                <ResultData results= {data}/>
            </Table.Body>
        </Table>
        
    )
}