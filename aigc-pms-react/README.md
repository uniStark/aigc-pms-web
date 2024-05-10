```shell

    #upload 1
    docker build --platform linux/amd64 -t beamstark/aigc-pms-web-react:3.0 .
    
    #upload 2
    docker push beamstark/aigc-pms-web-react:3.0
    
    #download1
    docker pull beamstark/aigc-pms-web-react:3.0
    
    #download2
    docker run -d -p 3000:80 --name aigc-pms-web-react_3.0 beamstark/aigc-pms-web-react:3.0
```

