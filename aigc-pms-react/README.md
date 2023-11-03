```shell
    #1
    docker build -t beamstark/aigc-pms-web-react:1.3 .
    
    #2
    docker push beamstark/aigc-pms-web-react:1.3
    
    #3
    docker run -d -p 3000:3000 --name aigc-pms-web-react_1.1 beamstark/aigc-pms-web-react:1.1
```

