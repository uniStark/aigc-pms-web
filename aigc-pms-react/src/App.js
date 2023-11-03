import './App.css';
import * as React from 'react'
import {Input, Select, Textarea, HStack} from '@chakra-ui/react';
import Module1 from "./module/Module1";
import MyButton1 from "./module/MyButton1";
import {useState} from "react";
import MyButton2 from "./module/MyButton2";

function App() {
    const [timeout, setTimeoutValue] = useState(120);
    const [threadCount, setThreadCountValue] = useState(8);

    // const [outFilePath, setOutFilePathValue] = useState('/Users/weirdo/Desktop/aigc-pms-out/');
    // const [apiKey, setApiKey] = useState('');
    // const [context, setContextValue] = useState('');
    // const [url, setUrlValue] = useState('');
    // const [csvFile, setCsvFileValue] = useState('/Users/weirdo/Desktop/aigc-pms-csv/in.csv');

    const [outFilePath, setOutFilePathValue] = useState('~/java-project/aigc-pms-web/src/data/out/');
    const [apiKey, setApiKey] = useState('app-80ESj8FvFcb6haLWh0GoGx1d');
    const [context, setContextValue] = useState('{"inputs": { "query": "$query"},"user": "user-001"}');
    const [url, setUrlValue] = useState('https://traimodel.pinming.cn/v1/workflow-messages');
    const [csvFile, setCsvFileValue] = useState('~/java-project/aigc-pms-web/src/data/in2.csv');

    const handleSubmit = async (e) => {
        console.log("Press the LUNCH Button.");
        e.preventDefault();

        const requestData = {
            timeout: Number(timeout),
            outFilePath,
            apiKey,
            context,
            url,
            csvFile,
            threadCount: Number(threadCount),
        };

        try{
            const response = await fetch('http://127.0.0.1:9321/aigc/todo', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(requestData),
            });

            if (!response.ok) {
                throw new Error('Request failed with status: ' + response.status);
            }

            const responseData = await response.json();
            console.log('Response from backend:', responseData);
            // 在这里处理响应数据

        } catch (error) {
            console.error('Error:', error);
            // 在这里处理错误
        }
    };

    const handleClick = () => {
        console.log("Press the stop button");
        fetch('http://127.0.0.1:9321/aigc/stop')
            .then(response => {
                if (!response.ok) {
                    throw new Error('请求失败');
                }
                return response.json();
            })
            .then(data => {
                // 处理响应数据
                console.log(data);
            })
            .catch(error => {
                // 处理错误
                console.error(error);
            });
    };
    return (
        <div className="box">
            <form onSubmit={handleSubmit}>
                <HStack className="box2">
                    <div className="box1">
                        <p>Timeout(s)</p>
                        <Input
                            htmlSize={5}
                            width='auto'
                            type="number"
                            value={timeout}
                            onChange={(e) => setTimeoutValue(e.target.value)}
                            placeholder='Timeout(s)'
                        />
                    </div>
                    <Module1 id="module1"/>
                </HStack>
                <div className="box1">
                    <p>URL</p>
                    <Input
                        type="text"
                        value={url}
                        onChange={(e) => setUrlValue(e.target.value)}
                        placeholder='Enter Your URL'
                    />
                </div>

                <div className="box1">
                    <p>Out File Path</p>
                    <Input
                        type="text"
                        value={outFilePath}
                        onChange={(e) => setOutFilePathValue(e.target.value)}
                        focusBorderColor='pink.400'
                        placeholder='Enter Your Out-File-Path'
                    />
                </div>

                <div className="box1">
                    <p>CSV File Path</p>
                    <Input
                        type="text"
                        value={csvFile}
                        onChange={(e) => setCsvFileValue(e.target.value)}
                        focusBorderColor='pink.400'
                        placeholder='Enter Your Out-File-Path'
                    />
                </div>

                <div className="box1">
                    <p>API Key</p>
                    <Input
                        type="text"
                        value={apiKey}
                        onChange={(e) => setApiKey(e.target.value)}
                        placeholder='Enter Your API Key'
                    />
                </div>
                <div className="box1">
                    <p>Context</p>
                    <Textarea
                        type="text"
                        value={context}
                        onChange={(e) => setContextValue(e.target.value)}
                        placeholder='Enter Your Context Code'
                    />
                </div>

                <div className="box1">
                    <p>Thread Count</p>
                    <Select
                        type="number"
                        value={threadCount}
                        onChange={(e) => setThreadCountValue(e.target.value)}
                        placeholder='Select Thread Count'>
                        <option value='1'>1</option>
                        <option value='4'>4</option>
                        <option value='8'>8</option>
                        <option value='16'>16</option>
                        <option value='32'>32</option>
                        <option value='64'>64</option>
                        <option value='128'>128</option>
                        <option value='256'>256</option>
                    </Select>
                </div>
                <MyButton1 type="submit"/>
            </form>
            <form onClick={handleClick}>
                <MyButton2 type="submit"/>
            </form>


            <h6>Designed in Stark</h6>
        </div>
    );
}

export default App;

